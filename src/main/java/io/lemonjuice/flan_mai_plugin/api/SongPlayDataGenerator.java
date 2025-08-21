package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.SongPlayDataRenderer;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
public class SongPlayDataGenerator {
    private static final String CACHE_PATH = "./cache/mai_play_data/";

    public static String generate(long qq, int songId) {
        File file = new File(CACHE_PATH + "qq" + qq + "_" + songId + ".png");
        try {
            SongPlayDataRenderer renderer = new SongPlayDataRenderer(qq, songId, file, ImageFormat.PNG);
            if(!renderer.renderAndOutput()) {
                return "";
            }
            return file.getPath();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("生成游玩记录失败！", e);
            return "";
        }
    }
}
