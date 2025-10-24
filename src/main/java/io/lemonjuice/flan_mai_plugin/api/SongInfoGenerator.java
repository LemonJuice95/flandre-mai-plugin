package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.SongInfoRenderer;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Log4j2
public class SongInfoGenerator {
    private static final String CACHE_PATH = "./cache/mai_song_info/";

    public static BufferedImage generate(int songId) {
        File file = new File(CACHE_PATH + songId + ".png");
        if(file.exists()) {
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                log.warn("图片缓存读取失败，即将重新渲染", e);
            }
        }
        try {
            SongInfoRenderer renderer = new SongInfoRenderer(songId);
            BufferedImage result = renderer.render();
            try {
                ImageIO.write(result, "PNG", file);
            } catch (IOException e) {
                log.warn("图片缓存失败", e);
            }
            return result;
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("生成歌曲信息失败！", e);
            return null;
        }
    }
}
