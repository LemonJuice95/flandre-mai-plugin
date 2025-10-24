package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.SongPlayDataRenderer;
import lombok.extern.log4j.Log4j2;

import java.awt.image.BufferedImage;
import java.io.File;

@Log4j2
public class SongPlayDataGenerator {
    public static BufferedImage generate(long qq, int songId) {
        try {
            SongPlayDataRenderer renderer = new SongPlayDataRenderer(qq, songId);
            return renderer.render();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("生成游玩记录失败！", e);
            return null;
        }
    }
}
