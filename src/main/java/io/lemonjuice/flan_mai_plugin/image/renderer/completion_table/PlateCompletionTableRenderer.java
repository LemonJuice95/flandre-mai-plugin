package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan.PlanList;
import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.model.Song;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.SongUtils;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Log4j2
public class PlateCompletionTableRenderer extends CompletionTableRenderer {
    private static final Color[] DATA_COLORS = {
            new Color(129, 217, 85, 255),
            new Color(245, 189, 21, 255),
            new Color(255 ,129, 141, 255),
            new Color(138, 0, 226, 255),
            new Color(159, 81, 220, 255),
    };

    private final String plateName;

    protected final Map<String, List<Song>> songMap = new TreeMap<>(SongUtils.levelComparator); //按倒序排序
    protected final Map<Integer, List<PlayRecord>> records = new HashMap<>();
    private final int width;
    private final int height;
    private final int totalNum;
    private int completedNum = 0;
    private int[] completedNumPerLevel = {0, 0, 0, 0};

    public PlateCompletionTableRenderer(String plateName, List<Integer> requirement, List<PlayRecord> records, File output, ImageFormat format) {
        super(PlanList.getPlan(plateName.substring(1)), output, format);
        this.plateName = plateName;

        records.forEach((r) -> {
            if(!this.records.containsKey(r.songId)) {
                this.records.put(r.songId, new ArrayList<>());
            }
            this.records.get(r.songId).add(r);
        });

        for(int id : requirement) {
            Song song = SongManager.getSongById(id);
            if(!this.songMap.containsKey(song.level.get(3))) {
                this.songMap.put(song.level.get(3), new ArrayList<>());
            }
            this.songMap.get(song.level.get(3)).add(song);
        }

        this.width = 1400;
        int bodyHeight = 0;
        int levelDistance = 15;
        int lineHeight = 115;
        for(List<Song> levelI : this.songMap.values()) {
            bodyHeight += levelDistance;
            bodyHeight += levelI.size() / 10 * lineHeight;
            bodyHeight += (levelI.size() % 10 != 0) ? lineHeight : 0;
        }
        bodyHeight -= levelDistance;
        this.height = 150 + bodyHeight + 360;

        this.totalNum = requirement.size();
    }

    @Override
    public BufferedImage render() {
        try {
            BufferedImage result = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            this.renderBg(g, this.width, this.height);
            this.renderPlateAndProgressBg(g);
            this.renderSongs(g);
            this.renderCompleteData(g);


            return result;
        } catch (Exception e) {
            log.error("绘制牌子完成表失败！", e);
            return null;
        }
    }

    private void renderCompleteData(Graphics2D g2d) throws IOException, FontFormatException {
        Font tb;
        Font siyuan;
        try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
            tb = Font.createFont(Font.TRUETYPE_FONT, input);
        }
        try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
            siyuan = Font.createFont(Font.TRUETYPE_FONT, input);
        }
        tb = tb.deriveFont(40.0F);
        siyuan = siyuan.deriveFont(35.0F);

        Graphics2D g = (Graphics2D) g2d.create();

        try {
            //总数完成度
            boolean totalCompleted = this.completedNum >= this.totalNum;
            g.setFont(totalCompleted ? siyuan : tb);
            g.setColor(new Color(124, 129, 255, 255));
            FontMetrics totalMetrics = g.getFontMetrics();
            String totalCompletionStr = totalCompleted ? "完成" : String.format("%d/%d", this.completedNum, this.totalNum);
            int totalXOffset = totalMetrics.stringWidth(totalCompletionStr);
            g.drawString(totalCompletionStr, 390 - totalXOffset, 280);

            //每个难度的完成度
            for(int i = 0; i < this.completedNumPerLevel.length; i++) {
                int completeNumI = this.completedNumPerLevel[i];
                boolean completed = completeNumI >= this.totalNum;
                g.setFont(completed ? siyuan : tb);
                g.setColor(DATA_COLORS[i]);
                FontMetrics metrics = g.getFontMetrics();
                String completionStr = completed ? "完成" : String.format("%d/%d", completeNumI, this.totalNum);
                int xOffset = metrics.stringWidth(completionStr);
                g.drawString(completionStr, 590 + 200 * i - xOffset, 280);
            }
        } finally {
            g.dispose();
        }
    }

    private void renderSongs(Graphics2D g) throws IOException, FontFormatException {
        int y = 375;

        BufferedImage levelIcon = ImageIO.read(new File(FileRefs.TABLE_LEVEL_BG));
        Font tb;
        try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
            tb = Font.createFont(Font.TRUETYPE_FONT, input);
        }
        tb = tb.deriveFont(35.0F);
        g.setFont(tb);
        FontMetrics levelMetrics = g.getFontMetrics();

        for(Map.Entry<String, List<Song>> e : this.songMap.entrySet()) {
            List<Song> songList = e.getValue();
            songList.sort((s1, s2) -> s2.ds.get(3).compareTo(s1.ds.get(3)));

            g.drawImage(levelIcon, 65, y, null);
            String level = e.getKey();
            int levelXOffset = levelMetrics.stringWidth(level) / 2;
            g.drawString(level, 113 - levelXOffset, y + 63);

            int lineCounter = 0;
            int x = 200;
            for(Song s : e.getValue()) {
                lineCounter++;

                this.renderSingleSong(g, s.id, x, y, this.records.get(s.id));

                if(lineCounter >= 10) {
                    lineCounter = 0;
                    x = 200;
                    y += 115;
                } else {
                    x += 115;
                }
            }
            if(lineCounter == 0) {
                y -= 115;
            }

            y += 130;
        }
    }

    private void renderSingleSong(Graphics2D g2d, int songId, int x, int y, List<PlayRecord> records) throws IOException, FontFormatException {
        if(this.tbFontCache == null) {
            try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
                this.tbFontCache = Font.createFont(Font.TRUETYPE_FONT, input);
            }
        }

        Graphics2D g = (Graphics2D) g2d.create();

        try {
            //曲绘
            BufferedImage cover = ImageIO.read(FileRefs.songCover(songId));
            g.drawImage(cover, x, y, 100, 100, null);

            //id背景
            g.setColor(new Color(124, 129, 255, 255));
            g.fillRect(x, y + 80, 100, 20);

            //id
            String idStr = String.valueOf(songId);
            this.tbFontCache = this.tbFontCache.deriveFont(20.0F);
            g.setFont(this.tbFontCache);
            g.setColor(Color.WHITE);
            FontMetrics idMetrics = g.getFontMetrics();
            int idXOffset = idMetrics.stringWidth(idStr) / 2;
            g.drawString(idStr, x + 50 - idXOffset, y + 97);

            //覆盖层
            if(records != null) {
                Boolean[] completed = {false, false, false, false};

                for (PlayRecord r : records) {
                    if(this.plan.validateCompleted(r) && r.levelIndex == 3) {
                        BufferedImage completeStatus = this.getImage(FileRefs.SONG_COMPLETED_BG);
                        g.drawImage(completeStatus, x, y, 100, 80, null);

                        BufferedImage recordIcon = this.getImage(this.plan.getIconPath(r));
                        int iconWidth = this.plan.getIconWidth();
                        int iconHeight = this.plan.getIconHeight();
                        g.drawImage(recordIcon, x + 50 - iconWidth / 2, y + 40 - iconHeight / 2, iconWidth, iconHeight, null);
                    }
                }

                for (PlayRecord r : records) {
                    if(this.plan.validateCompleted(r) && r.levelIndex < 4) {
                        this.completedNumPerLevel[r.levelIndex]++;
                        BufferedImage completeLevel = this.getImage(FileRefs.songCompletedIcon(r.levelIndex));
                        g.drawImage(completeLevel, x + 5 + 25 * r.levelIndex, y + 65, null);
                        completed[r.levelIndex] = true;
                    }
                }

                if(Arrays.stream(completed).filter(b -> b).count() == 4) {
                    this.completedNum++;
                }
            }
        } finally {
            g.dispose();
        }
    }


    private void renderPlateAndProgressBg(Graphics2D g) throws IOException{
        //进度背景
        BufferedImage progressBg = ImageIO.read(new File(FileRefs.PLATE_PROGRESS_BG));
        g.drawImage(progressBg, 185, 20, null);

        //牌子
        BufferedImage plate = ImageIO.read(new File(FileRefs.PLATE_DIR + this.plateName + ".png"));
        g.drawImage(plate, 200, 35, 1000, 161, null);
    }
}
