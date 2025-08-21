package io.lemonjuice.flan_mai_plugin.refence;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileRefs {
    public static final String IMAGE_DIR = "./images/mai/";
    public static final String PIC_DIR = imageDir("pic/");
    public static final String PLATE_DIR = imageDir("plate/");
    public static final String SONG_COVER_DIR = imageDir("cover/");

    public static final String B50_BG = picDir("b50_bg.png");
    public static final String LOGO = picDir("logo.png");
    public static final String NAME = picDir("Name.png");
    public static final String RATING_CALC_BG = picDir("UI_CMN_Shougou_Rainbow.png");
    public static final String DEFAULT_PLATE = picDir("UI_Plate_300501.png");
    public static final String DEFAULT_AVATAR = picDir("UI_Icon_309503.png");
    public static final String DEFAULT_CLASS = picDir("UI_FBR_Class_25.png");

    public static final String RANK_D = picDir("UI_TTR_Rank_D.png");
    public static final String RANK_C = picDir("UI_TTR_Rank_C.png");
    public static final String RANK_B = picDir("UI_TTR_Rank_B.png");
    public static final String RANK_BB = picDir("UI_TTR_Rank_BB.png");
    public static final String RANK_BBB = picDir("UI_TTR_Rank_BBB.png");
    public static final String RANK_A = picDir("UI_TTR_Rank_A.png");
    public static final String RANK_AA = picDir("UI_TTR_Rank_AA.png");
    public static final String RANK_AAA = picDir("UI_TTR_Rank_AAA.png");
    public static final String RANK_S = picDir("UI_TTR_Rank_S.png");
    public static final String RANK_SP = picDir("UI_TTR_Rank_Sp.png");
    public static final String RANK_SS = picDir("UI_TTR_Rank_SS.png");
    public static final String RANK_SSP = picDir("UI_TTR_Rank_SSp.png");
    public static final String RANK_SSS = picDir("UI_TTR_Rank_SSS.png");
    public static final String RANK_SSSP = picDir("UI_TTR_Rank_SSSp.png");

    public static final String RATING_WHITE = picDir("UI_CMN_DXRating_01.png");
    public static final String RATING_BLUE = picDir("UI_CMN_DXRating_02.png");
    public static final String RATING_GREEN = picDir("UI_CMN_DXRating_03.png");
    public static final String RATING_YELLOW = picDir("UI_CMN_DXRating_04.png");
    public static final String RATING_RED = picDir("UI_CMN_DXRating_05.png");
    public static final String RATING_PURPLE = picDir("UI_CMN_DXRating_06.png");
    public static final String RATING_COPPER = picDir("UI_CMN_DXRating_07.png");
    public static final String RATING_SILVER = picDir("UI_CMN_DXRating_08.png");
    public static final String RATING_GOLD = picDir("UI_CMN_DXRating_09.png");
    public static final String RATING_PLATINUM = picDir("UI_CMN_DXRating_10.png");
    public static final String RATING_RAINBOW = picDir("UI_CMN_DXRating_11.png");

    public static final String B50_LEVEL_LABEL_BASIC = picDir("b50_score_basic.png");
    public static final String B50_LEVEL_LABEL_ADVANCED = picDir("b50_score_advanced.png");
    public static final String B50_LEVEL_LABEL_EXPERT = picDir("b50_score_expert.png");
    public static final String B50_LEVEL_LABEL_MASTER = picDir("b50_score_master.png");
    public static final String B50_LEVEL_LABEL_REMASTER = picDir("b50_score_remaster.png");

    public static final String SONG_TYPE_SD = picDir("SD.png");
    public static final String SONG_TYPE_DX = picDir("DX.png");

    public static final String SONG_INFO_BG = picDir("song_bg.png");
    public static final String PLAY_DATA_BG = picDir("info_bg.png");

    public static final String FC_FS = picDir("fcfs.png");
    public static final String RA_DX = picDir("ra-dx.png");

    public static final String SIYUAN_FONT = "./fonts/ResourceHanRoundedCN-Bold.ttf";
    public static final String TB_FONT = "./fonts/Torus SemiBold.otf";

    public static final Map<String, String> SONG_CATEGORY_MAP = new HashMap<>() {{
        put("流行&动漫", "anime");
        put("其他游戏", "game");
        put("舞萌", "maimai");
        put("niconico & VOCALOID", "niconico");
        put("音击&中二节奏", "ongeki");
        put("东方Project", "touhou");
        put("宴会場", "utage");
    }};

    public static String infoCategoryByRaw(String infoRaw) throws NullPointerException {
        String category = SONG_CATEGORY_MAP.get(infoRaw);
        return picDir("info-" + category + ".png");
    }

    public static String dsBg(int index) {
        if(index < 0 || index > 4) {
            throw new IllegalArgumentException("index must be in 0-4, but got: " + index);
        }
        return picDir(String.format("d-%d.png", index));
    }

    public static String playBonus(String bonus) {
        return picDir(String.format("UI_CHR_PlayBonus_%s.png", bonus.toUpperCase()));
    }

    public static String course(int course) {
        if(course < 0 || course > 22) {
            throw new IllegalArgumentException("course must be in 0-22, but got: " + course);
        }
        if(course > 10) {
            course += 1;
        }
        return picDir(String.format("UI_DNM_DaniPlate_%02d.png", course));
    }

    public static String playStatusIcon(String status) {
        return picDir(String.format("UI_MSS_MBase_Icon_%s.png", status.toUpperCase()));
    }

    public static String dxScoreStars(int num) {
        if(num < 1 || num > 5) {
            throw new IllegalArgumentException("num must be in 1-5, but got: " + num);
        }
        return picDir(String.format("UI_GAM_Gauge_DXScoreIcon_%02d.png", num));
    }

    public static String ratingNum(int num) {
        if(num < 0 || num > 9) {
            throw new IllegalArgumentException("num must be in 0-9, but got: " + num);
        }
        return picDir(String.format("UI_NUM_Drating_%d.png", num));
    }

    public static File songCover(int id) {
        int songId = id;
        String coverPath = SONG_COVER_DIR + songId + ".png";
        File coverFile = new File(coverPath);
        if(!coverFile.exists() && songId > 100000) {
            songId %= 10000;
            coverPath = SONG_COVER_DIR + songId + ".png";
            coverFile = new File(coverPath);
        }
        if(!coverFile.exists()) {
            if(songId < 10000) {
                songId += 10000;
            } else if(songId > 10000) {
                songId -= 10000;
            }
            coverPath = SONG_COVER_DIR + songId + ".png";
            coverFile = new File(coverPath);
        }
        if(!coverFile.exists()) {
            songId = 11000;
            coverPath = SONG_COVER_DIR + songId + ".png";
            coverFile = new File(coverPath);
        }
        return coverFile;
    }

    private static String picDir(String path) {
        return PIC_DIR + path;
    }

    private static String imageDir(String path) {
        return IMAGE_DIR + path;
    }
}
