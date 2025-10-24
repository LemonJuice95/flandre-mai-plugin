package io.lemonjuice.flan_mai_plugin.api;

import io.lemonjuice.flan_mai_plugin.exception.NotInitializedException;
import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.B50ImageRenderer;
import io.lemonjuice.flan_mai_plugin.service.MaiMaiProberService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.*;

@Log4j2
public class DivingFishB50Generator {
    public static BufferedImage generate(long qq) {
        try {
            JSONObject json = MaiMaiProberService.requestB50(qq);
            B50ImageRenderer renderer = new B50ImageRenderer(qq, json);
            return renderer.render();
        } catch (Exception e) {
            if(e instanceof NotInitializedException) {
                throw e;
            }
            log.error("生成B50失败！");
            return null;
        }
    }
}
