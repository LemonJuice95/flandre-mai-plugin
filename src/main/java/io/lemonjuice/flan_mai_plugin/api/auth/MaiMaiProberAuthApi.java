package io.lemonjuice.flan_mai_plugin.api.auth;

import io.lemonjuice.flan_mai_plugin.exception.DivingFishException;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.utils.DigestUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
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
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class MaiMaiProberAuthApi {
    private static final ConcurrentHashMap<Long, CachedDivingFishToken> TOKEN_CACHE = new ConcurrentHashMap<>();

    public static String getToken(long qq) throws DivingFishException {
        return TOKEN_CACHE.compute(qq, (k, v) -> {
            if (v == null) {
                return getNewToken(qq);
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (v.getValidUntilMillis() <= currentTimeMillis) {
                return getNewToken(qq);
            }
            return v;
        }).getToken();
    }

    private static CachedDivingFishToken getNewToken(long qq) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(AuthEndPoint.TOKEN.getValue());

            JSONObject requestDataRaw = new JSONObject();
            requestDataRaw.put("grant_type", "urn:diving-fish:params:oauth:grant-type:on-behalf-of");
            requestDataRaw.put("client_id", ConfigRefs.DIVING_FISH_CLIENT_ID.get());
            requestDataRaw.put("client_secret", ConfigRefs.DIVING_FISH_CLIENT_SECRET.get());
            requestDataRaw.put("subject", genSubjectRef(qq));
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
            requestDataRaw.put("binding_label", String.format("%s%s", qqStr.substring(0, 5), "*".repeat(Math.max(0, qqStr.length() - 5))));
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
        return DigestUtils.sha256(String.format("%d:%s", qq, ConfigRefs.DIVING_FISH_CLIENT_ID.get()));
    }

    public static void init() {
        AuthEndPoint.init();
    }
}
