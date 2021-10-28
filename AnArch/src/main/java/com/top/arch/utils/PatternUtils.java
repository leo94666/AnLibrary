package com.top.arch.utils;

import java.util.regex.Pattern;

/**
 * 正则表达式工具
 */
public class PatternUtils {

    private String TAG = "PatternUtils";

    /**
     * 正则匹配手机号
     *
     * @param userPhone
     * @return
     */
    public static boolean patternMatcherPhone(String userPhone) {
        String pattern = "^[1]+[3-9]+\\d{9}";
        boolean matches = Pattern.matches(pattern, userPhone);
        return matches;
    }
}
