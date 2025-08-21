package io.lemonjuice.flan_mai_plugin.image.renderer;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.refence.Credits;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.RatingUtils;
import io.lemonjuice.flan_mai_plugin.utils.StringUtils;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

@Log4j2
public class SongInfoRenderer extends OutputtedImageRenderer {
    private final int songId;

    public SongInfoRenderer(int songId, File output, ImageFormat format) {
        super(output, format);
        this.songId = songId;
    }

    @Override
    public BufferedImage render() {
        try {
            Song song = SongManager.getSongById(this.songId);
            Font siyuan;
            Font tb;
            Color defaultColor = new Color(124, 130, 255, 255);
            try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
                tb = Font.createFont(Font.TRUETYPE_FONT, input);
            }
            try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
                siyuan = Font.createFont(Font.TRUETYPE_FONT, input);
            }

            BufferedImage bg = ImageIO.read(new File(FileRefs.SONG_INFO_BG));

            BufferedImage result = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            //背景
            g.drawImage(bg, 0, 0, null);

            //Logo
            BufferedImage logo = ImageIO.read(new File(FileRefs.LOGO));
            g.drawImage(logo, 65, 25, 249, 120, null);

            //曲绘
            BufferedImage cover = ImageIO.read(FileRefs.songCover(this.songId));
            g.drawImage(cover, 128, 195, 250, 250, null);

            //标题
            String title = song.title;
            if(StringUtils.getDisplayLength(title) > 40) {
                title = StringUtils.cutByDisplayLength(title, 39) + "...";
            }
            siyuan = siyuan.deriveFont(28.0F);
            g.setFont(siyuan);
            g.setColor(defaultColor);
            g.drawString(title, 405, 228);

            //曲师
            String artist = song.info.artist;
            if(StringUtils.getDisplayLength(artist) > 50) {
                artist = StringUtils.cutByDisplayLength(artist, 49) + "...";
            }
            siyuan = siyuan.deriveFont(20.0F);
            g.setFont(siyuan);
            g.drawString(artist, 407, 275);

            //bpm
            String bpm = String.valueOf(song.info.bpm);
            tb = tb.deriveFont(30.0F);
            g.setFont(tb);
            g.drawString(bpm, 460, 341);

            //ID
            String id = String.format("ID %d", this.songId);
            tb = tb.deriveFont(30.0F);
            g.setFont(tb);
            g.drawString(id, 407, 433);

            //流派
            String cat = "分类";
            siyuan = siyuan.deriveFont(28.0F);
            g.setFont(siyuan);
            g.drawString(cat, 640, 390);

            String category = song.info.category;
            siyuan = siyuan.deriveFont(24.0F);
            g.setFont(siyuan);
            FontMetrics categoryMetrics = g.getFontMetrics();
            int categoryXOffset = categoryMetrics.stringWidth(category) / 2;
            g.drawString(category, 669 - categoryXOffset, 432);

            //谱面类型
            if(!song.info.category.equals("宴会場")) {
                String typePath = song.type.equals("DX") ? FileRefs.SONG_TYPE_DX : FileRefs.SONG_TYPE_SD;
                BufferedImage typePic = ImageIO.read(new File(typePath));
                g.drawImage(typePic, 411, 365, 88, 33, null);
            }

            //版本
            String versionPath = FileRefs.PIC_DIR + song.info.from + ".png";
            BufferedImage versionPic = ImageIO.read(new File(versionPath));
            g.drawImage(versionPic, 800, 355, 182, 90, null);

            //等级（定数）
            tb = tb.deriveFont(28.0F);
            g.setFont(tb);
            g.setColor(Color.WHITE);
            FontMetrics metrics28 = g.getFontMetrics();
            for(int i = 0; i < song.charts.size(); i++) {
                String level = String.format("%s(%.1f)", song.level.get(i), song.ds.get(i));
                int levelXOffset = metrics28.stringWidth(level) / 2;
                g.drawString(level, 181 - levelXOffset, 619 + 73 * i);
            }

            //填写上方表格
            g.setColor(defaultColor);
            for(int i = 0; i < song.charts.size(); i++) {
                Song.Chart chart = song.charts.get(i);
                int totalNotes = 0;

                //拟合定数
                float fitDiff = chart.fitDIff;
                String fitDiffStr = fitDiff != -1 ? String.format("%.2f", fitDiff) : "-";
                int fitXOffset = metrics28.stringWidth(fitDiffStr) / 2;
                g.drawString(fitDiffStr, 315 - fitXOffset, 608 + 73 * i);

                //物量
                for (int j = 0; j < chart.notes.size(); j++) {
                    int notes = chart.notes.get(j);
                    String notesStr = String.valueOf(notes);
                    int notesXOffset = metrics28.stringWidth(notesStr) / 2;
                    g.drawString(notesStr, 556 + 119 * j - notesXOffset, 608 + 73 * i);
                    totalNotes += notes;
                }

                String totalStr = String.valueOf(totalNotes);
                int totalXOffset = metrics28.stringWidth(totalStr) / 2;
                g.drawString(totalStr, 437 - totalXOffset, 608 + 73 * i);
            }

            //填写下方表格
            siyuan = siyuan.deriveFont(18.0F);
            tb = tb.deriveFont(25.0F);
            for(int i = 2; i < song.charts.size(); i++) {
                Song.Chart chart = song.charts.get(i);

                //谱师
                String author = chart.author;
                if(StringUtils.getDisplayLength(author) > 19) {
                    author = StringUtils.cutByDisplayLength(author, 18) + "...";
                }
                g.setFont(siyuan);
                FontMetrics authorMetric = g.getFontMetrics();
                int authorXOffset = authorMetric.stringWidth(author) / 2;
                g.drawString(author, 370 - authorXOffset, 1037 + 47 * (i - 2));

                //单曲Rating
                g.setFont(tb);
                FontMetrics ratingMetric = g.getFontMetrics();
                for(int j = Rank.SSSP.ordinal(), k = 0; j >= Rank.S.ordinal(); j--, k++) {
                    String rating = String.valueOf(RatingUtils.calcSongRating(song.ds.get(i), Rank.values()[j]));
                    int ratingXOffset = ratingMetric.stringWidth(rating) / 2;
                    g.drawString(rating, 536 + 101 * k - ratingXOffset, 1039 + 47 * (i - 2));
                }
            }

            //Credits
            String line1 = Credits.LINE1;
            String line2 = Credits.LINE2;
            siyuan = siyuan.deriveFont(14.0F);
            g.setFont(siyuan);
            FontMetrics creditsMetrics = g.getFontMetrics();
            int line1XOffset = creditsMetrics.stringWidth(line1) / 2;
            int line2XOffset = creditsMetrics.stringWidth(line2) / 2;
            int yDistance = creditsMetrics.getHeight();
            g.drawString(line1,600 - line1XOffset, 1207);
            g.drawString(line2, 600 - line2XOffset, 1207 + yDistance);

            return result;
        } catch (Exception e) {
            log.error("歌曲信息图片处理失败！", e);
        }
        return null;
    }
}
