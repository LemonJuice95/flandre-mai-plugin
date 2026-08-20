package io.lemonjuice.flan_mai_plugin.api.auth;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

@Log4j2
enum AuthEndPoint {
    TOKEN("token_endpoint"),
    DEVICE_AUTHORIZATION("device_authorization_endpoint");

    private final String key;
    @Getter
    private String value = "";

    private AuthEndPoint(String key) {
        this.key = key;
    }

    public static void init() {
        log.info("正在初始化水鱼OAuth端点...");
        JSONObject responseJson;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://auth.diving-fish.com/.well-known/openid-configuration");
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("初始化水鱼OAuth端点失败! (HTTP ERROR {})", response.getStatusLine().getStatusCode());
            }
            responseJson = new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException | JSONException e){
            log.error("初始化水鱼OAuth端点失败!", e);
            return;
        }

        for(AuthEndPoint ep : AuthEndPoint.values()) {
            ep.value = responseJson.optString(ep.key, "");
        }
    }
}
