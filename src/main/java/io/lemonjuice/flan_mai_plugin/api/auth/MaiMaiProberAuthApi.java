package io.lemonjuice.flan_mai_plugin.api.auth;

import io.lemonjuice.flan_mai_plugin.exception.DivingFishException;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.utils.DigestUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class MaiMaiProberAuthApi {
    private static final ConcurrentHashMap<Long, CompletableFuture<CachedDivingFishToken>> TOKEN_CACHE = new ConcurrentHashMap<>();
    private static final Thread CACHE_CLEANER_THREAD = new Thread(MaiMaiProberAuthApi::cleanCache, "Diving Fish Token Cleaner");

    @SuppressWarnings("BusyWait")
    private static void cleanCache() {
        while(true) {
            try {
                Thread.sleep(ConfigRefs.DIVING_FISH_TOKEN_CLEAN_RATE.get() * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            long currentTime = System.currentTimeMillis();
            TOKEN_CACHE.entrySet().removeIf(entry -> {
                CompletableFuture<CachedDivingFishToken> future = entry.getValue();
                if (future.isDone()) {
                    try {
                        CachedDivingFishToken token = future.getNow(null);
                        if (token != null) {
                            return token.getValidUntilMillis() <= currentTime;
                        }
                    } catch (CompletionException e) {
                        return true;
                    }
                }
                return false;
            });
        }
    }

    public static String getToken(long qq) throws DivingFishException {
        CompletableFuture<CachedDivingFishToken> future = TOKEN_CACHE.compute(qq, (k, v) -> {
            if(v != null) {
                if(v.isDone()) {
                    try {
                        CachedDivingFishToken token = v.getNow(null);
                        if(token != null && token.getValidUntilMillis() > System.currentTimeMillis()) {
                            return v;
                        }
                    } catch (CompletionException ignored) {
                    }
                } else {
                    return v;
                }
            }

            CompletableFuture<CachedDivingFishToken> newFuture = new CompletableFuture<>();
            Thread.startVirtualThread(() -> {
                try {
                    CachedDivingFishToken token = getNewToken(qq);
                    newFuture.complete(token);
                } catch (Throwable throwable) {
                    newFuture.completeExceptionally(throwable);
                }
            });
            return newFuture;
        });

        try {
            CachedDivingFishToken token = future.join();
            return token.getToken();
        } catch (CompletionException e) {
            Throwable throwable = e.getCause();
            if(throwable instanceof DivingFishException de) {
                throw de;
            }
            throw new DivingFishException();
        }
    }

    private static CachedDivingFishToken getNewToken(long qq) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(AuthEndPoint.TOKEN.getValue());
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(10000)
                    .setSocketTimeout(10000)
                    .build();
            post.setConfig(config);

            JSONObject requestDataRaw = new JSONObject();
            requestDataRaw.put("grant_type", "urn:diving-fish:params:oauth:grant-type:on-behalf-of");
            requestDataRaw.put("client_id", ConfigRefs.DIVING_FISH_CLIENT_ID.get());
            requestDataRaw.put("client_secret", ConfigRefs.DIVING_FISH_CLIENT_SECRET.get());
            requestDataRaw.put("subject", "ref:" + genSubjectRef(qq));
            requestDataRaw.put("scope", ConfigRefs.DIVING_FISH_SCOPES.get());
            String requestData = encodeData(requestDataRaw);
            post.setEntity(new StringEntity(requestData, ContentType.APPLICATION_FORM_URLENCODED));

            HttpResponse response = client.execute(post);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            if(response.getStatusLine().getStatusCode() != 200) {
                if(responseJson.has("error")) {
                    String errorStr = responseJson.getString("error");
                    log.error("生成新token失败! ({})", errorStr);
                    if(errorStr.equals("consent_required")) {
                        throw new DivingFishException.Unbound(errorStr);
                    }
                    throw new DivingFishException(errorStr);
                } else {
                    log.error("生成新token失败! (HTTP ERROR {})", response.getStatusLine().getStatusCode());
                    throw new DivingFishException();
                }
            }

            return new CachedDivingFishToken(
                    responseJson.getString("access_token"),
                    qq,
                    System.currentTimeMillis() + responseJson.getLong("expires_in") * 1000L - 10000L
            );
        } catch (IOException | JSONException e) {
            log.error("生成新token失败!", e);
            throw new DivingFishException();
        }
    }

    public static JSONObject bindRequest(long qq) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(AuthEndPoint.DEVICE_AUTHORIZATION.getValue());

            JSONObject requestDataRaw = new JSONObject();
            String qqStr = String.valueOf(qq);
            requestDataRaw.put("client_id", ConfigRefs.DIVING_FISH_CLIENT_ID.get());
            requestDataRaw.put("client_secret", ConfigRefs.DIVING_FISH_CLIENT_SECRET.get());
            requestDataRaw.put("scope", ConfigRefs.DIVING_FISH_SCOPES.get());
            requestDataRaw.put("subject_ref", genSubjectRef(qq));
            requestDataRaw.put("binding_label", String.format("QQ %s%s", qqStr.substring(0, 5), "*".repeat(Math.max(0, qqStr.length() - 5))));
            String requestData = encodeData(requestDataRaw);
            post.setEntity(new StringEntity(requestData, ContentType.APPLICATION_FORM_URLENCODED));

            HttpResponse response = client.execute(post);
            JSONObject responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
            if(response.getStatusLine().getStatusCode() != 200) {
                if(responseJson.has("error")) {
                    log.error("发起绑定请求失败! ({})", responseJson.getString("error"));
                    throw new DivingFishException(responseJson.getString("error"));
                } else {
                    log.error("发起绑定请求失败! (HTTP ERROR {})", response.getStatusLine().getStatusCode());
                    throw new DivingFishException();
                }
            }
            return responseJson;
        } catch (IOException | JSONException e) {
            log.error("发起绑定请求失败!", e);
            throw new DivingFishException();
        }
    }

    private static String encodeData(JSONObject src) {
        StringBuilder result = new StringBuilder();
        for(String key : src.keySet()) {
            result.append(
                    String.format("%s=%s",
                        URLEncoder.encode(key, StandardCharsets.UTF_8),
                        URLEncoder.encode(src.get(key).toString(), StandardCharsets.UTF_8)
                    )
            );
            result.append("&");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String genSubjectRef(long qq) {
        return DigestUtils.sha256(String.format("%s:%d", ConfigRefs.DIVING_FISH_CLIENT_ID.get(), qq));
    }

    public static void init() {
        AuthEndPoint.init();
        CACHE_CLEANER_THREAD.start();
    }
}
