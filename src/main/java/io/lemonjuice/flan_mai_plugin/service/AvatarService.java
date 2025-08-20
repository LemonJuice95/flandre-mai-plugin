package io.lemonjuice.flan_mai_plugin.service;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Log4j2
public class AvatarService {
    @Nullable
    public static BufferedImage getAvatarByQQ(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = URI.create(String.format("https://q1.qlogo.cn/g?b=qq&nk=%d&s=100", qq));
            HttpGet get = new HttpGet(uri);
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("qq头像拉取失败！qq:{}", qq);
                return null;
            }
            try (InputStream input = new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()))) {
                return ImageIO.read(input);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}
