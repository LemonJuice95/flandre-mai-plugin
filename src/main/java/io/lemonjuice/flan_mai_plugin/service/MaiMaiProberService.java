package io.lemonjuice.flan_mai_plugin.service;

import io.lemonjuice.flan_mai_plugin.exception.InvalidTokenException;
import io.lemonjuice.flan_mai_plugin.exception.TokenTooMuchUsageException;
import io.lemonjuice.flan_mai_plugin.refence.CacheFileRefs;
import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Log4j2
public class MaiMaiProberService {
    private static final String DEVELOPER_TOKEN_HEADER_NAME = "Developer-Token";
    private static final String URL = "https://www.diving-fish.com/api/maimaidxprober/";

    @Nullable
    public static JSONObject requestB50(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(urlWithEndpoint("query/player"));
            JSONObject body = new JSONObject();
            body.put("qq", qq);
            body.put("b50", true);
            StringEntity requestEntity = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("B50拉取失败！qq:{}", qq);
                return null;
            }
            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    @Nullable
    public static JSONArray requestPlayerRecords(long qq) {
        JSONObject json = requestPlayDataGet(qq);
        if(json != null && json.has("records")) {
            return json.getJSONArray("records");
        }
        return null;
    }

    @Nullable
    public static JSONObject requestPlayDataGet(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String endpoint = String.format("dev/player/records?qq=%d", qq);
            HttpGet get = new HttpGet(urlWithEndpoint(endpoint));
            get.addHeader(DEVELOPER_TOKEN_HEADER_NAME, ConfigRefs.DIVING_FISH_TOKEN.get());
            HttpResponse response = httpClient.execute(get);
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject json;
            try {
                json = new JSONObject(responseStr);
            } catch (JSONException e) {
                json = null;
            }

            if(response.getStatusLine().getStatusCode() == 400) {
                if (json != null && json.has("msg")) {
                    if (json.getString("msg").equals("请先联系水鱼申请开发者token") ||
                            json.getString("msg").equals("开发者token有误")) {
                        throw new InvalidTokenException("token无效");
                    }
                    //XXX 不太确定
                    if (json.getString("msg").equals("开发者token被禁用")) {
                        throw new TokenTooMuchUsageException("token使用次数到达上限");
                    }
                }
                return null;
            } else if(response.getStatusLine().getStatusCode() != 200) {
                log.error("游玩记录拉取失败！qq:{}", qq);
                return null;
            }

            return json;
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    @Nullable
    public static JSONObject requestPlayDataPost(long qq, int songId) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(urlWithEndpoint("dev/player/record"));

            post.setHeader(DEVELOPER_TOKEN_HEADER_NAME, ConfigRefs.DIVING_FISH_TOKEN.get());

            JSONObject body = new JSONObject();
            body.put("qq", qq);
            body.put("music_id", songId);
            StringEntity requestEntity = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject json;
            try {
                json = new JSONObject(responseStr);
            } catch (JSONException e) {
                json = null;
            }

            if(response.getStatusLine().getStatusCode() == 400) {
                if(json != null && json.has("msg")) {
                    if (json.getString("msg").equals("请先联系水鱼申请开发者token") ||
                        json.getString("msg").equals("开发者token有误")) {
                        throw new InvalidTokenException("token无效");
                    }
                    //XXX 不太确定
                    if(json.getString("msg").equals("开发者token被禁用")) {
                        throw new TokenTooMuchUsageException("token使用次数到达上限");
                    }
                }
                return null;
            } else if(response.getStatusLine().getStatusCode() != 200) {
                log.error("游玩记录拉取失败！qq:{}, 歌曲id:{}", qq, songId);
                return null;
            }
            return json;
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    public static JSONObject requestTestPlayData() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(urlWithEndpoint("/player/test_data"));
            HttpResponse response = httpClient.execute(get);
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject json;
            try {
                json = new JSONObject(responseStr);
            } catch (JSONException e) {
                json = null;
            }

            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("测试游玩记录拉取失败！");
                return null;
            }

            return json;
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    @Nullable
    public static JSONObject requestChartStats() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(urlWithEndpoint("chart_stats"));
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("获取谱面信息失败！疑似网络异常");
                return null;
            }
            JSONObject outer = new JSONObject(EntityUtils.toString(response.getEntity()));
            return outer.getJSONObject("charts");
        } catch (IOException e) {
            log.error("获取谱面信息失败！", e);
        }
        return null;
    }

    @Nullable
    public static JSONArray requestSongAlias() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://www.yuzuchan.moe/api/maimaidx/maimaidxalias");
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("获取歌曲别名失败！疑似网络异常");
                return null;
            }
            return new JSONArray(new JSONObject(EntityUtils.toString(response.getEntity())).getJSONArray("content"));
        } catch (IOException e) {
            log.error("获取歌曲别名失败！", e);
        }
        return null;
    }

    @Nullable
    public static JSONArray requestSongListRaw() {
        File etagFile = new File(CacheFileRefs.SONG_DATA_ETAG);
        File cacheFile = new File(CacheFileRefs.SONG_DATA_CACHE);
        String etag = "";

        if(etagFile.exists()) {
            try (FileInputStream input = new FileInputStream(etagFile);
                 InputStreamReader reader = new InputStreamReader(input);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                etag = bufferedReader.readLine();
            } catch (IOException e) {
                log.error("获取缓存歌曲信息标识符失败", e);
            }
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(urlWithEndpoint("music_data"));
            if(!etag.isEmpty()) {
                get.addHeader("If-None-Match", etag);
            }
            HttpResponse response = httpClient.execute(get);

            if(response.getStatusLine().getStatusCode() != 200) { //使用本地缓存
                boolean netError = response.getStatusLine().getStatusCode() != 304;
                if(netError) {
                    log.error("获取歌曲列表失败！疑似网络异常");
                }

                try (FileInputStream input = new FileInputStream(cacheFile)) {
                    JSONTokener jsonTokener = new JSONTokener(input);
                    JSONObject json = new JSONObject(jsonTokener);
                    return json.getJSONArray("music_data");
                } catch (Exception e) {
                    log.error("无法使用本地缓存的歌曲信息", e);
                }

                if(!netError) {
                    get = new HttpGet(urlWithEndpoint("music_data"));
                    response = httpClient.execute(get);
                } else {
                    return null;
                }
            }

            JSONArray jsonArray = new JSONArray(EntityUtils.toString(response.getEntity()));
            //输出到本地缓存
            if(!cacheFile.getParentFile().exists()) cacheFile.getParentFile().mkdirs();
            JSONObject outputJson = new JSONObject();
            outputJson.put("music_data", jsonArray);
            try (FileOutputStream cacheOutput = new FileOutputStream(cacheFile);
                 FileOutputStream etagOutput = new FileOutputStream(etagFile)) {
                cacheOutput.write(outputJson.toString().getBytes(StandardCharsets.UTF_8));
                etagOutput.write(response.getFirstHeader("etag").getValue().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.error("缓存歌曲信息失败", e);
            }

            return jsonArray;
        } catch (IOException e) {
            log.error("获取歌曲列表失败！", e);
        }
        return null;
    }

    private static String urlWithEndpoint(String suffix) {
        return URL + suffix;
    }
}
