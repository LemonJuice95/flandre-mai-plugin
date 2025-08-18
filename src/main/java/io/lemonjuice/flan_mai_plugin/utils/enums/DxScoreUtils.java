package io.lemonjuice.flan_mai_plugin.utils.enums;

public class DxScoreUtils {
    public static int getStarNum(int score, int totalScore) {
        int rate = score * 100 / totalScore;
        if(rate <= 85)
            return 0;
        else if(rate <= 90)
            return 1;
        else if(rate <= 93)
            return 2;
        else if(rate <= 95)
            return 3;
        else if(rate <= 97)
            return 4;
        else
            return 5;
    }
}
