package com.ddblock.mybatis.spring.plus.util;

/**
 * Author XiaoJia
 * Date 2019-03-12 16:14
 */
public class StringUtil {
    private static final String DB_SPLIT = "_";
    private static final char DB_SPLIT_CHAR = '_';

    /**
     * 判断字符串对象是否为空或则长度为零（没有执行trim()方法）
     *
     * @param s 字符串对象
     *
     * @return 是否为空或空串
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 将Java中属性名、类名的命名规范相应转为数据库中字段名、表名的命名规范
     *
     * @param originName 符合Java中属性名、类名的命名规范的字符串
     *
     * @return 符合数据库中字段名、表名的命名规范的字符串
     */
    public static String formatToDBName(String originName) {
        if (isEmpty(originName))
            return null;

        // 如果以"_"开头，则不处理
        if (originName.startsWith(DB_SPLIT)) {
            return originName;
        }

        StringBuilder sb = new StringBuilder();
        char[] chars = originName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c >= 65 && c <= 90) {
                if (i != 0)
                    sb.append(DB_SPLIT_CHAR);
                c = (char) (c + 32);
            }
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * 将数据库中字段名、表名的的命名规范相应转为Java中属性名、类名命名规范
     *
     * @param originName 符合数据库中字段名、表名的命名规范的字符串
     *
     * @return 符合Java中属性名、类名的命名规范的字符串
     */
    public static String formatToJavaName(String originName) {
        if (isEmpty(originName))
            return null;

        // 如果以"_"开头，则不处理
        if (originName.startsWith(DB_SPLIT)) {
            return originName;
        }

        StringBuilder sb = new StringBuilder();
        char[] chars = originName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == DB_SPLIT_CHAR) {
                i++;
                c = (char) (chars[i] - 32);
            }
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * 将字符串的首字母大写
     *
     * @param originName 原字符串
     *
     * @return 转换后字符串
     */
    public static String firstCharToUpperCase(String originName) {
        String firstChar = originName.substring(0, 1);
        String otherStr = originName.substring(1);
        return firstChar.toUpperCase() + otherStr;
    }

    /**
     * 将字符串的首字母小写
     *
     * @param originName 原字符串
     *
     * @return 转换后字符串
     */
    public static String firstCharToLowerCase(String originName) {
        String firstChar = originName.substring(0, 1);
        String otherStr = originName.substring(1);
        return firstChar.toLowerCase() + otherStr;
    }

}
