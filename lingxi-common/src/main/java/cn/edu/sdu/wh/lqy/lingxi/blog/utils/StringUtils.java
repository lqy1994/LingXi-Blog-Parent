package cn.edu.sdu.wh.lqy.lingxi.blog.utils;

public class StringUtils {

    public static boolean isNull(String str) {
        return str == null || str.trim().length() == 0 || "null".equals(str);
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isBlank(String str) {
        return org.apache.commons.lang3.StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
