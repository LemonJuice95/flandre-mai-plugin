package io.lemonjuice.flan_mai_plugin.b50;

import io.lemonjuice.flan_mai_plugin.refence.ConfigRefs;
import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import io.lemonjuice.flan_mai_plugin.song.SongManager;
import io.lemonjuice.flan_mai_plugin.utils.StringUtils;
import io.lemonjuice.flan_mai_plugin.utils.enums.DxScoreUtils;
import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;
import io.lemonjuice.flan_mai_plugin.utils.enums.RatingFrame;
import io.lemonjuice.flan_mai_plugin.utils.enums.SongLevelLabel;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;

@Log4j2
public class DivingFishB50Generator {
    private static final Color[] idColors = {
            new Color(129, 217, 85, 255),
            new Color(245, 189, 21, 255),
            new Color(255, 129, 141, 255),
            new Color(138, 0, 226, 255),
            new Color(159, 81, 220, 255)
    };

    public static void generate(long qq) {
        JSONObject json = divingFishRequest(qq);
        outputToCache(drawB50(json, qq), qq);
    }

    private static BufferedImage drawB50(JSONObject json, long qq) {
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
            if(!json.getString("plate").isEmpty()) {
                platePath = FileRefs.PLATE_DIR + json.getString("plate") + ".png";
            }
            BufferedImage plate = ImageIO.read(new File(platePath));
            g.drawImage(plate, 300, 60, 800, 130, null);

            //头像
            String avatarPath = FileRefs.DEFAULT_AVATAR;
            BufferedImage avatar = ImageIO.read(new File(avatarPath));
            BufferedImage qqAvatar = getQQLogo(qq);
            g.drawImage(avatar, 305, 65, 120, 120, null);
            if(qqAvatar != null) {
                g.drawImage(qqAvatar, 308, 68, 114, 114, null);
            }

            //昵称
            BufferedImage nicknameBg = ImageIO.read(new File(FileRefs.NAME));
            g.drawImage(nicknameBg, 435, 115, null);
            try (InputStream input = DivingFishB50Generator.class.getClassLoader().getResourceAsStream(FileRefs.SIYUAN_FONT)) {
                String nickname = json.getString("nickname");
                Font nicknameFont = Font.createFont(Font.TRUETYPE_FONT, input).deriveFont(28.0F);
                g.setFont(nicknameFont);
                g.setColor(Color.BLACK);
                g.drawString(nickname, 445, 147);
                g.setColor(Color.WHITE);
            }

            //段位
            int courseLevel = json.getInt("additional_rating");
            BufferedImage course = ImageIO.read(new File(FileRefs.course(courseLevel)));
            g.drawImage(course, 625, 120, 80, 32, null);


            //Rating
            int rating = json.getInt("rating");
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
            JSONArray b35Json = json.getJSONObject("charts").getJSONArray("sd");
            for(int i = 0; i < b35Json.length(); i++) {
                JSONObject songJson = b35Json.getJSONObject(i);
                drawSong(songJson, g, 16 + 276 * (i % 5), 235 + 114 * (i / 5));
                b35Rating += songJson.getInt("ra");
            }

            //b15
            JSONArray b15Json = json.getJSONObject("charts").getJSONArray("dx");
            for(int i = 0; i < b15Json.length(); i++) {
                JSONObject songJson = b15Json.getJSONObject(i);
                drawSong(songJson, g, 16 + 276 * (i % 5), 1085 + 114 * (i / 5));
                b15Rating += songJson.getInt("ra");
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
            try (InputStream input = DivingFishB50Generator.class.getClassLoader().getResourceAsStream(FileRefs.TB_FONT)) {
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
            try (InputStream input = DivingFishB50Generator.class.getClassLoader().getResourceAsStream(FileRefs.SIYUAN_FONT)) {
                String credits = "Designed by Yuri-YuzuChaN & BlueDeer233. | Java ver. transformed by LemonJuice95";
                String credits2 =  "Generated by " + ConfigRefs.BOT_NAME + ".";
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

    private static void drawSong(JSONObject song, Graphics2D g, int x, int y) throws IOException, FontFormatException {
        int songId = song.getInt("song_id");
        Color fontColor = Color.WHITE;
        Font tbFont;
        Font siyuanFont;
        try (InputStream input = DivingFishB50Generator.class.getClassLoader().getResourceAsStream(FileRefs.TB_FONT)) {
            tbFont = Font.createFont(Font.TRUETYPE_FONT, input);
        }
        try (InputStream input = DivingFishB50Generator.class.getClassLoader().getResourceAsStream(FileRefs.SIYUAN_FONT)) {
            siyuanFont = Font.createFont(Font.TRUETYPE_FONT, input);
        }

        //歌曲背景
        SongLevelLabel levelLabel = SongLevelLabel.fromString(song.getString("level_label"));
        if (levelLabel == SongLevelLabel.REMASTER) {
            fontColor = new Color(138, 0, 226, 255);
        }
        BufferedImage labelBg = ImageIO.read(new File(levelLabel.getBgPic()));
        g.drawImage(labelBg, x, y, null);

        //曲绘
        File coverFile = getSongCover(songId);
        BufferedImage cover = ImageIO.read(coverFile);
        g.drawImage(cover, x + 12, y + 12, 75, 75, null);

        //谱面类型
        String type = song.getString("type");
        String typePicPath = type.equals("DX") ? FileRefs.SONG_TYPE_DX : FileRefs.SONG_TYPE_SD;
        BufferedImage typePic = ImageIO.read(new File(typePicPath));
        g.drawImage(typePic, x + 51, y + 91, 37, 14, null);

        //标题
        String title = song.getString("title");
        if(StringUtils.getDisplayLength(title) > 18) {
            title = StringUtils.cutByDisplayLength(title, 17);
            title += "...";
        }
        siyuanFont = siyuanFont.deriveFont(14.0F);
        g.setFont(siyuanFont);
        g.setColor(fontColor);
        g.drawString(title, x + 93, y + 18);

        //成绩
        String achievements = String.format("%.4f%%", song.getDouble("achievements"));
        tbFont = tbFont.deriveFont(30.0F);
        g.setFont(tbFont);
        g.setColor(fontColor);
        g.drawString(achievements, x + 93, y + 49);

        //评级
        Rank rank = Rank.fromString(song.getString("rate"));
        BufferedImage rankPic = ImageIO.read(new File(rank.getPicPath()));
        g.drawImage(rankPic, x + 92, y + 78, 63, 28, null);

        //dx分
        int dxScore = song.getInt("dxScore");
        int totalDxScore = SongManager.getSongById(songId)
                .charts.get(song.getInt("level_index"))
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
        float ds = song.getFloat("ds");
        int songRating = song.getInt("ra");
        String songRaStr = String.format("%.1f -> %d", ds, songRating);
        g.drawString(songRaStr, x + 93, y + 70);

        //FC AP
        try {
            String iconPath1 = FileRefs.playStatusIcon(song.getString("fc"));
            BufferedImage icon1 = ImageIO.read(new File(iconPath1));
            g.drawImage(icon1, x + 154, y + 77, 34, 34, null);
        } catch (Exception ignored) {
            //无该状态时不绘制即可
        }

        //FS
        try {
            String iconPath2 = FileRefs.playStatusIcon(song.getString("fs"));
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

    private static void outputToCache(BufferedImage b50Image, long qq) {
        try {
            File file = new File("./cache/mai_b50/b50_" + qq + ".png");
            if (file.exists()) {
                file.delete();
            }
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            ImageIO.write(b50Image, "PNG", file);
        } catch (IOException e) {
            log.error("输出图片失败！", e);
        }
    }

    @Nullable
    private static JSONObject divingFishRequest(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://www.diving-fish.com/api/maimaidxprober/query/player");
            JSONObject body = new JSONObject();
            body.put("qq", qq);
            body.put("b50", true);
            StringEntity requestEntity = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(post);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("B50拉取失败！qq:{}", qq);
                return null;
            }
            return new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    @Nullable
    private static BufferedImage getQQLogo(long qq) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = URI.create(String.format("https://q1.qlogo.cn/g?b=qq&nk=%d&s=100", qq));
            HttpGet get = new HttpGet(uri);
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() != 200) {
                log.error("qq头像拉取失败！qq:{}", qq);
                return null;
            }
            try (InputStream input = new ByteArrayInputStream(EntityUtils.toByteArray(response.getEntity()))) {
                return ImageIO.read(input);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    private static File getSongCover(int id) {
        int songId = id;
        String coverPath = FileRefs.SONG_COVER_DIR + songId + ".png";
        File coverFile = new File(coverPath);
        if(!coverFile.exists()) {
            if(songId > 100000) {
                songId -= 100000;
            } else if(songId < 10000) {
                songId += 10000;
            } else if(songId > 10000) {
                songId -= 10000;
            }
            coverPath = FileRefs.SONG_COVER_DIR + songId + ".png";
            coverFile = new File(coverPath);
        }
        if(!coverFile.exists()) {
            songId = 11000;
            coverPath = FileRefs.SONG_COVER_DIR + songId + ".png";
            coverFile = new File(coverPath);
        }
        return coverFile;
    }
}
