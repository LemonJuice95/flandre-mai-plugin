package io.lemonjuice.flan_mai_plugin.image.renderer;

import io.lemonjuice.flan_mai_plugin.image.ImageFormat;
import io.lemonjuice.flan_mai_plugin.model.PlayRecord;
import io.lemonjuice.flan_mai_plugin.refence.Credits;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.service.AvatarService;
import io.lemonjuice.flan_mai_plugin.utils.RecordUtils;
import io.lemonjuice.flan_mai_plugin.utils.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.DxScoreUtils;
import io.lemonjuice.flan_mai_plugin.utils.StringUtils;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import io.lemonjuice.flan_mai_plugin.utils.enums.RatingFrame;
import io.lemonjuice.flan_mai_plugin.utils.enums.SongLevelLabel;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Log4j2
public class B50ImageRenderer extends ImageRenderer {
    private static final Color[] idColors = {
            new Color(129, 217, 85, 255),
            new Color(245, 189, 21, 255),
            new Color(255, 129, 141, 255),
            new Color(138, 0, 226, 255),
            new Color(159, 81, 220, 255)
    };
    
    private final long qq;
    private final JSONObject b50Data;

    public B50ImageRenderer(long qq, JSONObject b50Data) {
        this.qq = qq;
        this.b50Data = b50Data;
    }

    @Override
    public BufferedImage render() {
        try {
            BufferedImage bg = ImageIO.read(new File(FileRefs.B50_BG));

            int b35Rating = 0;
            int b15Rating = 0;

            BufferedImage result = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = result.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            //背景
            g.drawImage(bg, 0, 0, null);

            //logo
            BufferedImage logo = ImageIO.read(new File(FileRefs.LOGO));
            g.drawImage(logo, 14, 60, 249, 120, null);

            //牌子
            String platePath = FileRefs.DEFAULT_PLATE;
            if(!this.b50Data.getString("plate").isEmpty()) {
                platePath = FileRefs.PLATE_DIR + this.b50Data.getString("plate") + ".png";
            }
            BufferedImage plate = ImageIO.read(new File(platePath));
            g.drawImage(plate, 300, 60, 800, 130, null);

            //头像
            String avatarPath = FileRefs.DEFAULT_AVATAR;
            BufferedImage avatar = ImageIO.read(new File(avatarPath));
            BufferedImage qqAvatar = AvatarService.getAvatarByQQ(this.qq);
            g.drawImage(avatar, 305, 65, 120, 120, null);
            if(qqAvatar != null) {
                g.drawImage(qqAvatar, 308, 68, 114, 114, null);
            }

            //昵称
            BufferedImage nicknameBg = ImageIO.read(new File(FileRefs.NAME));
            g.drawImage(nicknameBg, 435, 115, null);
            try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
                String nickname = this.b50Data.getString("nickname");
                Font nicknameFont = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(28.0F);
                g.setFont(nicknameFont);
                g.setColor(Color.BLACK);
                g.drawString(nickname, 445, 147);
                g.setColor(Color.WHITE);
            }

            //段位
            int courseLevel = this.b50Data.getInt("additional_rating");
            BufferedImage course = ImageIO.read(new File(FileRefs.course(courseLevel)));
            g.drawImage(course, 625, 120, 80, 32, null);


            //Rating
            int rating = this.b50Data.getInt("rating");
            int rating_ = rating;
            RatingFrame frame = RatingFrame.getFrameByRating(rating_);
            BufferedImage frameImage = ImageIO.read(new File(frame.getPicPath()));
            g.drawImage(frameImage, 435, 72, 186, 35, null);
            int digit = 4;
            do {
                BufferedImage numPic = ImageIO.read(new File(FileRefs.ratingNum(rating_ % 10)));
                g.drawImage(numPic, 520 + 15 * digit, 80, 17, 20, null);
                rating_ /= 10;
                digit--;
            } while (rating_ != 0);

            //b35
            JSONArray b35Json = this.b50Data.getJSONObject("charts").getJSONArray("sd");
            for(int i = 0; i < b35Json.length(); i++) {
                PlayRecord playRecord = RecordUtils.parsePlayRecord(b35Json.getJSONObject(i));
                drawSong(playRecord, g, 16 + 276 * (i % 5), 235 + 114 * (i / 5));
                b35Rating += playRecord.rating;
            }

            //b15
            JSONArray b15Json = this.b50Data.getJSONObject("charts").getJSONArray("dx");
            for(int i = 0; i < b15Json.length(); i++) {
                PlayRecord playRecord = RecordUtils.parsePlayRecord(b15Json.getJSONObject(i));
                drawSong(playRecord, g, 16 + 276 * (i % 5), 1085 + 114 * (i / 5));
                b15Rating += playRecord.rating;
            }

            //Rating计算
            BufferedImage ratingCalcBg = ImageIO.read(new File(FileRefs.RATING_CALC_BG));
            g.drawImage(ratingCalcBg, 435, 160, 270, 27, null);
            StringBuilder ratingCalc = new StringBuilder("B35: ");
            ratingCalc.append(b35Rating);
            ratingCalc.append(" + B15: ");
            ratingCalc.append(b15Rating);
            ratingCalc.append(" = ");
            ratingCalc.append(rating);
            try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
                Font ratingCalcFont = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(17.0F);
                g.setFont(ratingCalcFont);
                g.setColor(Color.BLACK);
                FontMetrics metrics = g.getFontMetrics();
                int x = 570 - metrics.stringWidth(ratingCalc.toString()) / 2;
                g.drawString(ratingCalc.toString(), x, 178);
                g.setColor(Color.WHITE);
            }

            //友人段位
            String classLevelPath = FileRefs.DEFAULT_CLASS;
            BufferedImage classLevel = ImageIO.read(new File(classLevelPath));
            g.drawImage(classLevel, 620, 60, 90, 54, null);

            //Credits
            try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
                String credits = Credits.LINE1;
                String credits2 = Credits.LINE2;
                Font creditsFont = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(14.0F);
                g.setFont(creditsFont);
                FontMetrics metrics = g.getFontMetrics();
                int xOffset = metrics.stringWidth(credits) / 2;
                int xOffset2 = metrics.stringWidth(credits2) / 2;
                int yDistance = metrics.getHeight();
                g.setColor(new Color(124, 129, 255, 255));
                g.drawString(credits, 700 - xOffset, bg.getHeight() - 33);
                g.drawString(credits2, 700 - xOffset2, bg.getHeight() - 33 + yDistance);
                g.setColor(Color.WHITE);
            }

            return result;
        } catch (Exception e) {
            log.error("图片处理失败！", e);
            return null;
        }
    }

    private void drawSong(PlayRecord playRecord, Graphics2D g, int x, int y) throws IOException, FontFormatException {
        int songId = playRecord.songId;
        Color fontColor = Color.WHITE;
        Font tbFont;
        Font siyuanFont;
        try (FileInputStream input = new FileInputStream(FileRefs.TB_FONT)) {
            tbFont = Font.createFont(Font.TRUETYPE_FONT, input);
        }
        try (FileInputStream input = new FileInputStream(FileRefs.SIYUAN_FONT)) {
            siyuanFont = Font.createFont(Font.TRUETYPE_FONT, input);
        }

        //歌曲背景
        SongLevelLabel levelLabel = playRecord.levelLabel;
        if (levelLabel == SongLevelLabel.REMASTER) {
            fontColor = new Color(138, 0, 226, 255);
        }
        BufferedImage labelBg = ImageIO.read(new File(levelLabel.getBgPic()));
        g.drawImage(labelBg, x, y, null);

        //曲绘
        File coverFile = FileRefs.songCover(songId);
        BufferedImage cover = ImageIO.read(coverFile);
        g.drawImage(cover, x + 12, y + 12, 75, 75, null);

        //谱面类型
        String type = playRecord.type;
        String typePicPath = type.equals("DX") ? FileRefs.SONG_TYPE_DX : FileRefs.SONG_TYPE_SD;
        BufferedImage typePic = ImageIO.read(new File(typePicPath));
        g.drawImage(typePic, x + 51, y + 91, 37, 14, null);

        //标题
        String title = playRecord.title;
        if(StringUtils.getDisplayLength(title) > 18) {
            title = StringUtils.cutByDisplayLength(title, 17);
            title += "...";
        }
        siyuanFont = siyuanFont.deriveFont(14.0F);
        g.setFont(siyuanFont);
        g.setColor(fontColor);
        g.drawString(title, x + 93, y + 18);

        //成绩
        String achievements = String.format("%.4f%%", playRecord.achievements);
        tbFont = tbFont.deriveFont(30.0F);
        g.setFont(tbFont);
        g.setColor(fontColor);
        g.drawString(achievements, x + 93, y + 49);

        //评级
        Rank rank = playRecord.rank;
        BufferedImage rankPic = ImageIO.read(new File(rank.getPicPath()));
        g.drawImage(rankPic, x + 92, y + 78, 63, 28, null);

        //dx分
        int dxScore = playRecord.dxScore;
        int totalDxScore = SongManager.getSongById(songId)
                .charts.get(playRecord.levelIndex)
                .notes.stream().mapToInt(Integer::intValue).sum() * 3;
        String dxScoreStr = String.format("%d/%d", dxScore, totalDxScore);
        tbFont = tbFont.deriveFont(15.0F);
        g.setFont(tbFont);
        g.setColor(fontColor);
        FontMetrics dxScoreMetrics = g.getFontMetrics();
        int dxScoreXOffset = dxScoreMetrics.stringWidth(String.valueOf(dxScoreStr)) / 2;
        g.drawString(dxScoreStr, x + 219 - dxScoreXOffset, y + 70);

        //星数
        int starNum = DxScoreUtils.getStarNum(dxScore, totalDxScore);
        if(starNum > 0) {
            String starPicPath = FileRefs.dxScoreStars(starNum);
            BufferedImage starPic = ImageIO.read(new File(starPicPath));
            g.drawImage(starPic, x + 217, y + 80, 47, 26, null);
        }

        //定数 -> 单曲rating
        float ds = playRecord.ds;
        int songRating = playRecord.rating;
        String songRaStr = String.format("%.1f -> %d", ds, songRating);
        g.drawString(songRaStr, x + 93, y + 70);

        //FC AP
        try {
            String iconPath1 = FileRefs.playStatusIcon(playRecord.fcStatus);
            BufferedImage icon1 = ImageIO.read(new File(iconPath1));
            g.drawImage(icon1, x + 154, y + 77, 34, 34, null);
        } catch (Exception ignored) {
            //无该状态时不绘制即可
        }

        //FS
        try {
            String iconPath2 = FileRefs.playStatusIcon(playRecord.syncStatus);
            BufferedImage icon2 = ImageIO.read(new File(iconPath2));
            g.drawImage(icon2, x + 185, y + 77, 34, 34, null);
        } catch (Exception ignored) {
            //无该状态时不绘制即可
        }


        //id
        tbFont = tbFont.deriveFont(13.0F);
        g.setFont(tbFont);
        g.setColor(idColors[levelLabel.ordinal()]);
        FontMetrics idMetrics = g.getFontMetrics();
        int idXOffset = idMetrics.stringWidth(String.valueOf(songId)) / 2;
        g.drawString(String.valueOf(songId), x + 26 - idXOffset, y + 102);
        g.setColor(Color.WHITE);
    }
}
