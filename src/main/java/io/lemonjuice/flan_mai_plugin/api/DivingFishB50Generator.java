package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.B50ImageRenderer;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.io.*;

@Log4j2
public class DivingFishB50Generator {
    public static boolean generate(long qq) {
        try {
            File file = new File("./cache/mai_b50/b50_" + qq + ".png");
            JSONObject json = MaiMaiProberService.requestB50(qq);
            B50ImageRenderer renderer = new B50ImageRenderer(qq, json, file, ImageFormat.PNG);
            return renderer.renderAndOutput();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("生成B50失败！");
            return false;
        }
    }
}
