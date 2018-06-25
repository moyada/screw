package cn.moyada.screw.utils;


import java.util.StringTokenizer;

/**
 * Created by xueyikang on 2017/2/23.
 */
public class StringUtil {

    private static String[] EMPTY_STRING_ARRAY = new String[0];

    public static boolean isEmpty(String str) {
        if(null == str) {
            return true;
        }
        int length = str.length();
        if(0 == length) {
            return true;
        }
        for (int index = 0; index < length; index++) {
            if(!Character.isSpaceChar(str.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <T> boolean isEmpty(T[] objs) {
        return null == objs || objs.length == 0;
    }

    public static String[] split(String str, String sperater) {
        StringTokenizer tokenizer = new StringTokenizer(str, sperater);
        int length = tokenizer.countTokens();
        if(0 == length) {
            return EMPTY_STRING_ARRAY;
        }

        String[] splits = new String[length];

        for (int index = 0; index < length; index++) {
            splits[index] = tokenizer.nextToken();
        }
        return splits;
    }

    public static boolean startWith(String target, String start) {
        int length = start.length();
        if(target.length() < length) {
            return false;
        }

        for (int index = 0; index < length; index++) {
            if(target.charAt(index) != start.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    public static boolean endWith(String target, String end) {
        int length = end.length();
        int strLen = target.length();
        if(strLen < length) {
            return false;
        }

        for (int index = length-1, strIndex = strLen-1; index > 0; index--, strIndex--) {
            if(target.charAt(strIndex) != end.charAt(index)) {
                return false;
            }
        }
        return true;
    }
}
