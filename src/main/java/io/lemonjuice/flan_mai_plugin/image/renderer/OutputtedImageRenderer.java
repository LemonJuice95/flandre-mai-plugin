package io.lemonjuice.flan_mai_plugin.image.renderer;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public abstract class OutputtedImageRenderer extends ImageRenderer {
    private final File output;
    private final ImageFormat format;

    public OutputtedImageRenderer(File output, ImageFormat format) {
        this.output = output;
        this.format = format;
    }

    public boolean renderAndOutput() {
        BufferedImage bufferedImage = this.render();
        return ImageUtils.outputImage(bufferedImage, this.output, this.format);
    }
}
