package io.lemonjuice.flan_mai_plugin.utils;

public class StringUtils {
    public static int getDisplayLength(String str) {
        if(str == null || str.isEmpty()) {
            return 0;
        }
        int length = 0;
        for(int i = 0; i < str.length(); i++) {
            length += getCharLength(str.charAt(i));
        }
        return length;
    }

    public static String cutByDisplayLength(String str, int length) {
        if(str == null) {
            return "";
        }
        if(getDisplayLength(str) <= length) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        int lengthP = 0;
        for(int i = 0; i < str.length(); i++) {
            lengthP += getCharLength(str.charAt(i));
            if(lengthP > length) {
                break;
            }
            result.append(str.charAt(i));
        }
        return result.toString();
    }

    public static int getCharLength(char c) {
        if (isFullWidthChar(c)) {
            return 2;
        } else {
            return 1;
        }
    }

    public static boolean isFullWidthChar(char c) {
        return (c >= '\u1100' && c <= '\u11FF') ||
                (c >= '\u2E80' && c <= '\uA4CF') ||
                (c >= '\uAC00' && c <= '\uD7AF') ||
                (c >= '\uF900' && c <= '\uFAFF') ||
                (c >= '\uFE30' && c <= '\uFE4F') ||
                (c >= '\uFF00' && c <= '\uFFEF');
    }
}
