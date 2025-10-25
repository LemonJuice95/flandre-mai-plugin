package io.lemonjuice.flan_mai_plugin.utils.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum MaiVersion {
    MAIMAI("真", "真", "maimai"),
    MAIMAI_PLUS("真", "真", "maimai PLUS"),
    GREEN("超", "超", "maimai GreeN"),
    GREEN_PLUS("檄", "檄", "maimai GreeN PLUS"),
    ORANGE("橙", "橙", "maimai ORANGE"),
    ORANGE_PLUS("晓暁", "暁", "maimai ORANGE PLUS"),
    PINK("桃", "桃", "maimai PiNK"),
    PINK_PLUS("樱櫻", "櫻", "maimai PiNK PLUS"),
    MURASAKI("紫", "紫", "maimai MURASAKi"),
    MURASAKI_PLUS("堇菫", "菫", "maimai MURASAKi PLUS"),
    MILK("白", "白", "maimai MiLK"),
    MILK_PLUS("雪", "雪", "MiLK PLUS"),
    FINALE("輝辉", "輝", "maimai FiNALE"),

    MAI("舞霸", "舞", ""),

    DX("熊", "熊", "maimai でらっくす"),
    DX_PLUS("华華", "華", "maimai でらっくす"),
    SPLASH("爽", "爽", "maimai でらっくす Splash"),
    SPLASH_PLUS("煌", "煌", "maimai でらっくす Splash"),
    UNIVERSE("宙", "宙", "maimai でらっくす UNiVERSE"),
    UNIVERSE_PLUS("星", "星", "maimai でらっくす UNiVERSE"),
    FESTIVAL("祭", "祭", "maimai でらっくす FESTiVAL"),
    FESTIVAL_PLUS("祝", "祝", "maimai でらっくす FESTiVAL"),
    BUDDIES("双", "双", "maimai でらっくす BUDDiES"),
    BUDDIES_PLUS("宴", "宴", "maimai でらっくす BUDDiES"),
    PRISM("镜", "镜", "maimai でらっくす PRiSM");

    @Getter
    private final String matchingNames;
    @Getter
    private final String mappingName;
    @Getter
    private final String englishName;

    private MaiVersion(String matchingNames, String mappingName, String englishName) {
        this.matchingNames = matchingNames;
        this.mappingName = mappingName;
        this.englishName = englishName;
    }

    public static List<MaiVersion> matchVersion(String versionName) {
        List<MaiVersion> result = new ArrayList<>();

        for(MaiVersion vi : MaiVersion.values()) {
            if(vi.matchingNames.contains(versionName)) {
                result.add(vi);
            }
        }
        return result;
    }
}
