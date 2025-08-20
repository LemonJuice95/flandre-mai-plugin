package io.lemonjuice.flan_mai_plugin.utils;

import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Log4j2
public class ImageUtils {
    public static boolean outputImage(BufferedImage image, File file, String format) {
        try {
            if(file.exists()) {
                file.delete();
            }
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ImageIO.write(image, format, file);
        } catch (IOException e) {
            log.error("输出图片失败！", e);
            return false;
        }
        return true;
    }
}
