package com.monyrama.util;

public class StringUtil {
    public static boolean emptyString(String str) {
        return (str == null || str.trim().length() == 0);
    }
}
