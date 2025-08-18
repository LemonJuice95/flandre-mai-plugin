package io.lemonjuice.flan_mai_plugin.utils.enums;

import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import lombok.Getter;

public enum Rank {
    D(FileRefs.RANK_D),
    C(FileRefs.RANK_C),
    B(FileRefs.RANK_B),
    BB(FileRefs.RANK_BB),
    BBB(FileRefs.RANK_BBB),
    A(FileRefs.RANK_A),
    AA(FileRefs.RANK_AA),
    AAA(FileRefs.RANK_AAA),
    S(FileRefs.RANK_S),
    SP(FileRefs.RANK_SP),
    SS(FileRefs.RANK_SS),
    SSP(FileRefs.RANK_SSP),
    SSS(FileRefs.RANK_SSS),
    SSSP(FileRefs.RANK_SSSP);

    @Getter
    private final String picPath;

    private Rank(String picPath) {
        this.picPath = picPath;
    }

    public static Rank fromString(String name) {
        return Rank.valueOf(name.toUpperCase());
    }
}
