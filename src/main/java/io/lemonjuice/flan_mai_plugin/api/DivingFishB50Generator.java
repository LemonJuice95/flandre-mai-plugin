package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.DivingFishException;
import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.renderer.B50ImageRenderer;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.awt.image.BufferedImage;

@Log4j2
public class DivingFishB50Generator {
    public static BufferedImage generate(long qq) {
        try {
            JSONObject json = MaiMaiProberApi.requestB50(qq);
            B50ImageRenderer renderer = new B50ImageRenderer(qq, json);
            return renderer.render();
        } catch (Exception e) {
            if(e instanceof NotInitializedException || e instanceof DivingFishException) {
                throw e;
            }
            log.error("生成B50失败！");
            return null;
        }
    }
}
