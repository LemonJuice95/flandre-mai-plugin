package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.SongInfoRenderer;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
public class SongInfoGenerator {
    private static final String CACHE_PATH = "./cache/mai_song_info/";

    public static boolean generate(int songId) {
        File file = new File(CACHE_PATH + songId + ".png");
        if(file.exists()) {
            return true;
        }
        try {
            SongInfoRenderer renderer = new SongInfoRenderer(songId, file, ImageFormat.PNG);
            return renderer.renderAndOutput();
        } catch (Exception e) {
            log.error("生成歌曲信息失败！");
            return false;
        }
    }
}
