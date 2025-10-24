package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.ImageRenderer;
import io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan.Plan;
import io.lemonjuice.flan_mai_plugin.refence.Credits;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class CompletionTableRenderer extends ImageRenderer {
    private static final Color topColor = new Color(124, 129, 255, 255);
    private static final Color middleColor = new Color(193, 247, 255, 255);
    private static final Color bottomColor = Color.WHITE;

    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    protected Font tbFontCache = null;

    protected final Plan plan;

    public CompletionTableRenderer(Plan plan) {
        this.plan = plan;
    }

    protected BufferedImage getImage(String path) throws IOException {
        if(this.imageCache.containsKey(path)) {
            return this.imageCache.get(path);
        }
        BufferedImage image = ImageIO.read(new File(path));
        imageCache.put(path, image);
        return image;
    }

    protected void renderBg(Graphics2D g2d, int width, int height) throws IOException, FontFormatException {
        Graphics2D g = (Graphics2D) g2d.create();
        try {
            //底色
            float middleHeight = height * 0.4F;
            GradientPaint top2Middle = new GradientPaint(
                    (float) width / 2, 0,
                    topColor,
                    (float) width / 2, middleHeight,
                    middleColor);
            GradientPaint middle2Bottom = new GradientPaint(
                    (float) width / 2, middleHeight,
                    middleColor,
                    (float) width / 2, height,
                    bottomColor
            );
            g.setPaint(top2Middle);
            g.fillRect(0, 0, width, (int) middleHeight);
            g.setPaint(middle2Bottom);
            g.fillRect(0, (int) middleHeight, width, height);

            //极光
            BufferedImage aurora = ImageIO.read(new File(FileRefs.AURORA));
            g.drawImage(aurora, 0, 0, width, (int)(((float) width / (float) aurora.getWidth()) * aurora.getHeight()), null);

            //闪光效果
            BufferedImage shines = ImageIO.read(new File(FileRefs.SHINES));
            g.drawImage(shines, 0, 0, width, (int)(((float) width / (float) shines.getWidth()) * shines.getHeight()), null);

            //彩虹
            BufferedImage rainbow = ImageIO.read(new File(FileRefs.RAINBOW));
            BufferedImage rainbow_bottom = ImageIO.read(new File(FileRefs.RAINBOW_BOTTOM));
            g.drawImage(rainbow, (width - 763) / 2, height - 643, null);
            g.drawImage(rainbow_bottom, (width - 1200) / 2, height - 343, 1200, 200, null);

            //点阵
            BufferedImage dotMatrixRaw = ImageIO.read(new File(FileRefs.DOT_MATRIX));
            BufferedImage dotMatrix = new BufferedImage(dotMatrixRaw.getWidth(), dotMatrixRaw.getHeight() + 7, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g1 = dotMatrix.createGraphics();
            g1.drawImage(dotMatrixRaw, 0, 0, null);
            g1.dispose();
            TexturePaint texture = new TexturePaint(dotMatrix, new Rectangle(0, 0, dotMatrix.getWidth(), dotMatrixRaw.getHeight()));
            g.setPaint(texture);
            g.fillRect(0, 0, width, height);

            //Credits
            Font siyuan;
            BufferedImage creditsBg = ImageIO.read(new File(FileRefs.CREDITS_BG));
            g.drawImage(creditsBg, (width - 1000) / 2, height - 113, null);
            String line1 = Credits.LINE1;
            String line2 = Credits.LINE2;
            try (InputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
                siyuan = Font.createFont(Font.TRUETYPE_FONT, input);
            }
            siyuan = siyuan.deriveFont(14.0F);
            g.setColor(new Color(124, 129, 255, 255));
            g.setFont(siyuan);
            FontMetrics creditsMetrics = g.getFontMetrics();
            int line1XOffset = creditsMetrics.stringWidth(line1) / 2;
            int line2XOffset = creditsMetrics.stringWidth(line2) / 2;
            int yDistance = creditsMetrics.getHeight();
            g.drawString(line1,width / 2 - line1XOffset, height - 75);
            g.drawString(line2, width / 2 - line2XOffset, height - 75 + yDistance);
        } finally {
            g.dispose();
        }
    }
}
