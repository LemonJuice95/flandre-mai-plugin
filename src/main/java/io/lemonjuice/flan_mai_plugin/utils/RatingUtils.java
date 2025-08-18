package io.lemonjuice.flan_mai_plugin.utils;

import io.lemonjuice.flan_mai_plugin.utils.enums.Rank;

public class RatingUtils {
    public static int calcSongRating(float diff, float achievement) {
        Rank rank = Rank.fromAchievement(achievement);
        return (int) Math.floor(diff * Math.min(100.5F, achievement) / 100.0F * rank.getFactor());
    }

    public static int calcSongRating(float diff, Rank rank) {
        return (int) Math.floor(diff * rank.getLeft() / 100.0F * rank.getFactor());
    }
}
