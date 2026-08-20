package io.lemonjuice.flan_mai_plugin.utils;

import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Log4j2
public class DigestUtils {
    public static String sha256(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return toHexStr(messageDigest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
            return "";
        }
    }

    public static String toHexStr(byte[] input) {
        StringBuilder result = new StringBuilder();
        for(byte b : input) {
            String hex = Integer.toHexString(b & 0xff);
            if(hex.length() == 1) {
                result.append("0");
            }
            result.append(hex);
        }

        return result.toString();
    }
}
