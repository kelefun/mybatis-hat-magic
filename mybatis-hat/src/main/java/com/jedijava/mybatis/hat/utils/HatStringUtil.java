package com.jedijava.mybatis.hat.utils;

import com.jedijava.mybatis.hat.constants.SymbolConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liukaiyang
 * @since 2019/8/26 21:38
 */
public class HatStringUtil {
    private static Pattern upperPattern = Pattern.compile("[A-Z]");
    private static Pattern underlinePattern = Pattern.compile("_[a-z]?");

    /**
     * 去除无用的字符串，只保留字母和数字
     *
     * @return
     */
    public static String letterOrDigit(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param s userName / UserName
     * @return user_name
     */
    public static String toUnderline(String s) {
        if (HatStringUtil.isEmpty(s)) {
            return "";
        }
        s = firstToLowerCase(s);

        Matcher matcher = upperPattern.matcher(s);
        StringBuffer sb = new StringBuffer();
        for (int i = 1; matcher.find(); i++) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 类的属性方法名 转换为 属性名称
     * getUsername  -> username
     *
     * @param methodName
     * @return
     */
    public static String methodToFieldName(String methodName) {
        if (HatStringUtil.isEmpty(methodName)) {
            return null;
        }
        String result = methodName;
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            result = methodName.substring(3);
        }
        if (methodName.startsWith("is")) {
            result = methodName.substring(2);
        }
        return firstToLowerCase(result);
    }

    /**
     * 首字母变小写
     *
     * @param s
     * @return
     */
    public static String firstToLowerCase(String s) {
        if (s != null && s.length() >= 1) {
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                first = Character.toLowerCase(first);
                s = first + s.substring(1);
            }
        }
        return s;
    }

    /**
     * 判断字符串是否是数字
     *
     * @param s
     * @return
     */
    public static boolean isNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        } else {
            int length = s.length();
            for (int i = 0; i < length; ++i) {
                if (!Character.isDigit(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 下划线转驼峰
     *
     * @param underline user_name_test / username
     * @return
     */
    public static String toCamel(String underline) {
        if (HatStringUtil.isEmpty(underline)) {
            return "";
        }
        underline = underline.toLowerCase();
        Matcher matcher = underlinePattern.matcher(underline);
        StringBuffer sb = new StringBuffer();
        for (int i = 1; matcher.find(); i++) {
            String match = matcher.group(0);
            //下划线如果在末尾, 忽略它
            if (SymbolConst.UNDERLINE.equals(match)) {
                continue;
            }
            matcher.appendReplacement(sb, match.substring(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String toFieldName(String columnName) {
        if (HatStringUtil.isEmpty(columnName)) {
            return "";
        }
        if (columnName.contains(SymbolConst.UNDERLINE)) {
            return toCamel(columnName);
        } else {
            return columnName.toLowerCase();
        }
    }

    /**
     * empty: 表示对象为空或长度为0的String
     *
     *
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * blank: 表示对象为空或长度为0的String、空格字符串
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
    public static boolean isBlank(Object str) {
        if(str==null){
            return true;
        }
        return str.toString().trim().length() == 0;
    }

    public static boolean isLowerCase(CharSequence str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        final int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLowerCase(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
