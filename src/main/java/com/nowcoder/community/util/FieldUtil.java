package com.nowcoder.community.util;

public class FieldUtil {

    public static String fieldCheck(String s) {
        /**
         * 校验输入字段 1.非空  2.长度  3.类型
         */
        String message = null;
        if (s.length() == 0) {
            message = "长度不能为空";
            return message;
        }
        if (s.length() < 5) {
            message = "密码长度至少在5位及以上!";
            return message;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!wordCheck(s.charAt(i))) {
                message = "密码仅能包含英文字母和数字!";
                return message;
            }
        }
        return message;

    }

    private static boolean wordCheck(char c) {
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
            return true;
        }
        return false;
    }


}
