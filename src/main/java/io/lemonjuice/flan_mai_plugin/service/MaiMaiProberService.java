package io.lemonjuice.flan_mai_plugin.service;

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
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;

@Log4j2
public class MaiMaiProberService {
    private static String URL = "https://www.diving-fish.com/api/maimaidxprober/";

    public static JSONObject requestB50(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(urlSuffix("query/player"));
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
    public static JSONObject requestChartStats() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(urlSuffix("chart_stats"));
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
    public static JSONArray requestSongListRaw() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(urlSuffix("music_data"));
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("获取歌曲列表失败！疑似网络异常");
                return null;
            }

            return new JSONArray(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error("获取歌曲列表失败！", e);
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

    private static String urlSuffix(String suffix) {
        return URL + suffix;
    }
}
