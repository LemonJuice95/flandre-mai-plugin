package io.lemonjuice.flan_mai_plugin.utils.enums;

import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import lombok.Getter;

public enum Rank {
    D(FileRefs.RANK_D, 0.0F, 50.0F, 6.4F),
    C(FileRefs.RANK_C, 50.0F, 60.0F, 8.0F),
    B(FileRefs.RANK_B, 60.0F, 70.0F, 9.6F),
    BB(FileRefs.RANK_BB,70.0F, 75.0F, 11.2F),
    BBB(FileRefs.RANK_BBB, 75.0F, 80.0F, 12.0F),
    A(FileRefs.RANK_A, 80.0F, 90.0F, 13.6F),
    AA(FileRefs.RANK_AA, 90.0F, 94.0F, 15.2F),
    AAA(FileRefs.RANK_AAA, 94.0F, 97.0F, 16.8F),
    S(FileRefs.RANK_S, 97.0F, 98.0F, 20.0F),
    SP(FileRefs.RANK_SP, 98.0F, 99.0F, 20.3F),
    SS(FileRefs.RANK_SS, 99.0F, 99.5F, 20.8F),
    SSP(FileRefs.RANK_SSP, 99.5F, 100.0F, 21.1F),
    SSS(FileRefs.RANK_SSS, 100.0F, 100.5F, 21.6F),
    SSSP(FileRefs.RANK_SSSP, 100.5F, 101.0F, 22.4F);

    @Getter
    private final String picPath;
    @Getter
    private final float factor;
    @Getter
    private final float left; //Included
    @Getter
    private final float right; //Excluded

    private Rank(String picPath, float left, float right, float factor) {
        this.picPath = picPath;
        this.factor = factor;
        this.left = left;
        this.right = right;
    }

    public static Rank fromString(String name) {
        return Rank.valueOf(name.toUpperCase());
    }

    public static Rank fromAchievement(float achievement) {
        for(int i = 0; i < Rank.values().length; i++) {
            Rank rank = Rank.values()[i];
            if(achievement >= rank.left && achievement < rank.right) {
                return rank;
            }
        }
        return Rank.SSSP;
    }
}
