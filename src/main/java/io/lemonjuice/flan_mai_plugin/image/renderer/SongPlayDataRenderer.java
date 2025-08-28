package io.lemonjuice.flan_mai_plugin.image.renderer;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.refence.Credits;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.utils.DxScoreUtils;
import io.lemonjuice.flan_mai_plugin.utils.RecordUtils;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.StringUtils;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SongPlayDataRenderer extends OutputtedImageRenderer {
    private static final Color textColor = new Color(124, 129, 255, 255);

    private final long qq;
    private final int songId;
    private final List<PlayRecord> records;
    private Font siyuan;
    private Font tb;

    public SongPlayDataRenderer(long qq, int songId, File output, ImageFormat format) {
        super(output, format);
        this.qq = qq;
        this.songId = songId;
        this.records = RecordUtils.searchRecordBySongId(this.qq, this.songId);
    }

    @Override
    public BufferedImage render() {
        try {
            Song song = SongManager.getSongById(this.songId);
            List<Boolean> played = song.charts.stream().map((chart) -> false).collect(Collectors.toList());
            try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
                this.tb = Font.createFont(Font.TRUETYPE_FONT, input);
            }
            try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
                this.siyuan = Font.createFont(Font.TRUETYPE_FONT, input);
            }

            BufferedImage bg = ImageIO.read(new File(FileRefs.PLAY_DATA_BG));

            BufferedImage result = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            this.drawBgAndLogo(g, bg);
            this.drawCoverAndCategory(g, song);
            this.drawType(song, g);
            this.drawVersion(song, g);
            this.drawArtist(song, g);
            this.drawTitle(song, g);
            this.drawIdAndBPM(g, song);
            this.drawRecords(g, song, played);

            //定数 & 未游玩
            int colDistance = 100;
            this.tb = this.tb.deriveFont(25.0F);
            this.siyuan = this.siyuan.deriveFont(30.0F);
            for(int i = 0; i < played.size(); i++) {
                //定数背景
                BufferedImage dsBg = ImageIO.read(new File(FileRefs.dsBg(i)));
                g.drawImage(dsBg, 650, 235 + i * 100, null);

                //定数
                String dsStr = String.valueOf(song.ds.get(i));

                g.setColor(Color.WHITE);
                g.setFont(this.tb);
                FontMetrics dsMetrics = g.getFontMetrics();
                int dsXOffset = dsMetrics.stringWidth(dsStr) / 2;
                g.drawString(dsStr, 685 - dsXOffset, 259 + colDistance * i);

                if(!played.get(i)) {
                    String noData = "尚未游玩";
                    g.setFont(this.siyuan);
                    g.setColor(textColor);
                    FontMetrics noDataMetrics = g.getFontMetrics();
                    int noDataXOffset = noDataMetrics.stringWidth(noData) / 2;
                    g.drawString(noData, 800 - noDataXOffset, 312 + colDistance * i);
                }
            }

            if(played.size() < 5) {
                this.drawNoChart(g, played, colDistance);
            }

            //Credits
            String line1 = Credits.LINE1;
            String line2 = Credits.LINE2;
            siyuan = siyuan.deriveFont(14.0F);
            g.setColor(textColor);
            g.setFont(siyuan);
            FontMetrics creditsMetrics = g.getFontMetrics();
            int line1XOffset = creditsMetrics.stringWidth(line1) / 2;
            int line2XOffset = creditsMetrics.stringWidth(line2) / 2;
            int yDistance = creditsMetrics.getHeight();
            g.drawString(line1,600 - line1XOffset, 823);
            g.drawString(line2, 600 - line2XOffset, 823 + yDistance);

            return result;
        } catch (Exception e) {
            log.error("歌曲信息图片处理失败！", e);
        }
        return null;
    }

    private void drawRecord(Graphics2D g, int index, PlayRecord record, Song song) throws IOException {
        int colDistance = 100;

        this.drawAchivement(g, index, record, colDistance);
        this.drawRank(g, index, record, colDistance);
        this.drawRating(g, index, record, colDistance);
        this.drawDxScore(g, index, record, song, colDistance);
        this.drawFCFS(g, index, record, colDistance);
    }

    private void drawNoChart(Graphics2D g, List<Boolean> played, int colDistance) {
        //没有该难度
        String noChartStr = "没有该难度";
        this.siyuan = this.siyuan.deriveFont(30.0F);
        g.setColor(textColor);
        g.setFont(this.siyuan);
        FontMetrics noChartMetrics = g.getFontMetrics();
        int noChartXOffset = noChartMetrics.stringWidth(noChartStr) / 2;
        for(int i = played.size(); i < 5; i++) {
            g.drawString(noChartStr, 800 - noChartXOffset, 315 + i * colDistance);
        }
    }

    private void drawFCFS(Graphics2D g, int index, PlayRecord record, int colDistance) throws IOException {
        //FC & FS
        BufferedImage fcFsBg = ImageIO.read(new File(FileRefs.FC_FS));
        g.drawImage(fcFsBg, 965, 265 + colDistance * index, null);

        //FC
        try {
            String fcIconPath = FileRefs.playBonus(record.fcStatus);
            BufferedImage fcIcon = ImageIO.read(new File(fcIconPath));
            g.drawImage(fcIcon, 960, 261 + colDistance * index, 65, 65, null);
        } catch (Exception ignored) {
            //无该状态时不绘制
        }

        //FS
        try {
            String fsIconPath = FileRefs.playBonus(record.syncStatus);
            BufferedImage fsIcon = ImageIO.read(new File(fsIconPath));
            g.drawImage(fsIcon, 1025, 261 + colDistance * index, 65, 65, null);
        } catch (Exception ignored) {
            //无该状态时不绘制
        }
    }

    private void drawRecords(Graphics2D g, Song song, List<Boolean> played) throws IOException {
        //记录
        for(PlayRecord record : this.records) {
            int levelIndex = record.levelIndex;
            this.drawRecord(g, levelIndex, record, song);
            played.set(levelIndex, true);
        }
    }

    private void drawIdAndBPM(Graphics2D g, Song song) {
        this.tb = this.tb.deriveFont(22.0F);
        g.setFont(tb);
        FontMetrics metricsTb22 = g.getFontMetrics();

        //id
        String idStr = String.valueOf(this.songId);
        int idXOffset = metricsTb22.stringWidth(idStr) / 2;
        g.drawString(idStr, 160 - idXOffset, 730);

        //bpm
        String bpmStr = String.valueOf(song.info.bpm);
        int bpmXOffset = metricsTb22.stringWidth(bpmStr) / 2;
        g.drawString(bpmStr, 380 - bpmXOffset, 730);
    }

    private void drawTitle(Song song, Graphics2D g) {
        //标题
        String title = song.title;
        if(StringUtils.getDisplayLength(title) > 38) {
            title = StringUtils.cutByDisplayLength(title, 37) + "...";
        }
        this.siyuan = this.siyuan.deriveFont(20.0F);
        g.setFont(this.siyuan);
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleXOffset = titleMetrics.stringWidth(title) / 2;
        g.drawString(title, 255 - titleXOffset, 624);
    }

    private void drawArtist(Song song, Graphics2D g) {
        //曲师
        String artist = song.info.artist;
        if(StringUtils.getDisplayLength(artist) > 58) {
            artist = StringUtils.cutByDisplayLength(artist, 57) + "...";
        }
        this.siyuan = this.siyuan.deriveFont(16.0F);
        g.setFont(this.siyuan);
        g.setColor(textColor);
        FontMetrics artistMetrics = g.getFontMetrics();
        int artistXOffset = artistMetrics.stringWidth(artist) / 2;
        g.drawString(artist, 255 - artistXOffset, 599);
    }

    private void drawVersion(Song song, Graphics2D g) throws IOException {
        //版本
        String versionPath = FileRefs.PIC_DIR + song.info.from + ".png";
        BufferedImage versionPic = ImageIO.read(new File(versionPath));
        g.drawImage(versionPic, 295, 205, 183, 90, null);
    }

    private void drawType(Song song, Graphics2D g) throws IOException {
        //类型
        if(!song.info.category.equals("宴会場")) {
            String typePath = song.type.equals("DX") ? FileRefs.SONG_TYPE_DX : FileRefs.SONG_TYPE_SD;
            BufferedImage typePic = ImageIO.read(new File(typePath));
            g.drawImage(typePic, 350, 560, 55, 20, null);
        }
    }

    private void drawCoverAndCategory(Graphics2D g, Song song) throws IOException {
        //曲绘
        BufferedImage cover = ImageIO.read(FileRefs.songCover(this.songId));
        g.drawImage(cover, 100, 260, 300, 300, null);

        //流派
        try {
            BufferedImage category = ImageIO.read(new File(FileRefs.infoCategoryByRaw(song.info.category)));
            g.drawImage(category, 100, 260, null);
        } catch (Exception ignored) {
            //出现异常时不绘制
        }
    }

    private void drawBgAndLogo(Graphics2D g, BufferedImage bg) throws IOException {
        //背景
        g.drawImage(bg, 0, 0, null);
        //Logo
        BufferedImage logo = ImageIO.read(new File(FileRefs.LOGO));
        g.drawImage(logo, 0, 34, 249, 120, null);
    }

    private void drawDxScore(Graphics2D g, int index, PlayRecord record, Song song, int colDistance) throws IOException {
        //dx分
        int dxScore = record.dxScore;
        int totalDxScore = song.charts.get(index).notes.stream().mapToInt(Integer::intValue).sum() * 3;
        String dxScoreStr = String.format("%d/%d", dxScore, totalDxScore);
        this.tb = this.tb.deriveFont(13.0F);
        g.setFont(this.tb);
        FontMetrics dxScoreMetric = g.getFontMetrics();
        int dxScoreXOffset = dxScoreMetric.stringWidth(dxScoreStr) / 2;
        g.drawString(dxScoreStr, 916 - dxScoreXOffset, 309 + colDistance * index);

        //星数
        try {
            int starNum = DxScoreUtils.getStarNum(dxScore, totalDxScore);
            String starPath = FileRefs.dxScoreStars(starNum);
            BufferedImage starPic = ImageIO.read(new File(starPath));
            g.drawImage(starPic, 851, 296 + colDistance * index, 32, 19, null);
        } catch (IllegalArgumentException ignored) {
            //零星时不绘制星数
        }
    }

    private void drawRating(Graphics2D g, int index, PlayRecord record, int colDistance) throws IOException {
        //rating背景
        BufferedImage raDX = ImageIO.read(new File(FileRefs.RA_DX));
        g.drawImage(raDX, 850, 272 + colDistance * index, null);

        //单曲rating
        String ratingStr = String.valueOf(record.rating);
        this.tb = this.tb.deriveFont(18.0F);
        g.setFont(this.tb);
        FontMetrics raMetric = g.getFontMetrics();
        int raXOffset = raMetric.stringWidth(ratingStr) / 2;
        g.drawString(ratingStr, 915 - raXOffset, 290 + colDistance * index);
    }

    private void drawRank(Graphics2D g, int index, PlayRecord record, int colDistance) throws IOException {
        //评级
        Rank rank = record.rank;
        BufferedImage rankPic = ImageIO.read(new File(rank.getPicPath()));
        g.drawImage(rankPic, 737, 272 + colDistance * index, 100, 45, null);
    }

    private void drawAchivement(Graphics2D g, int index, PlayRecord record, int colDistance) {
        //成绩
        String achievements = String.format("%.4f%%", record.achievements);
        this.tb = this.tb.deriveFont(42.0F);
        g.setFont(this.tb);
        g.setColor(textColor);
        g.drawString(achievements, 510, 310 + index * colDistance);
    }
}
