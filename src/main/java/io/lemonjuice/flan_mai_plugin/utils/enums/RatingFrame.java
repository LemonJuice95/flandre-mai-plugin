package io.lemonjuice.flan_mai_plugin.utils.enums;

import io.lemonjuice.flan_mai_plugin.refence.FileRefs;
import lombok.Getter;

public enum RatingFrame {
    WHITE(0, 1000, FileRefs.RATING_WHITE),
    BLUE(1000, 2000, FileRefs.RATING_BLUE),
    GREEN(2000, 4000, FileRefs.RATING_GREEN),
    YELLOW(4000, 7000, FileRefs.RATING_YELLOW),
    RED(7000, 10000, FileRefs.RATING_RED),
    PURPLE(10000, 12000, FileRefs.RATING_PURPLE),
    COPPER(12000, 13000, FileRefs.RATING_COPPER),
    SILVER(13000, 14000, FileRefs.RATING_SILVER),
    GOLD(14000, 14500, FileRefs.RATING_GOLD),
    PLATINUM(14500, 15000, FileRefs.RATING_PLATINUM),
    RAINBOW(15000, 99999, FileRefs.RATING_RAINBOW);

    private final int left; //Included
    private final int right; //Excluded

    @Getter
    private final String picPath;

    private RatingFrame(int left, int right, String picPath) {
        this.left = left;
        this.right = right;
        this.picPath = picPath;
    }

    public static RatingFrame getFrameByRating(int rating) {
        for(int i = 0; i < RatingFrame.values().length - 1; i++) {
            RatingFrame value = RatingFrame.values()[i];
            if(rating >= value.left && rating < value.right) {
                return value;
            }
        }
        return RAINBOW;
    }
}
