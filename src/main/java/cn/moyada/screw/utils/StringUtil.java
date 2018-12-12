package cn.moyada.screw.utils;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xueyikang on 2017/2/23.
 */
public interface StringUtil {

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

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String[] split(String str, String sperater) {
        StringTokenizer tokenizer = new StringTokenizer(str, sperater);
        int length = tokenizer.countTokens();
        if(0 == length) {
            return new String[0];
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

    public static char[] getValue(String str) {
        int length = str.length();
        char[] value = new char[length];
        for (int index = 0; index < length; index++) {
            value[index] = str.charAt(index);
        }
        return value;
    }

    /**
     * 过滤空元素
     *
     * @param strs   对象数组
     * @return 过滤后数组
     */
    public static String[] filterEmpty(final String[] strs) {
        if(null == strs || 0 == strs.length) {
            return new String[0];
        }
        return Arrays.stream(strs)
                .filter(str -> !StringUtil.isEmpty(str))
                .distinct()
                .toArray(String[]::new);
    }

    /**
     * 过滤空元素
     *
     * @param strs   对象数组
     * @return 过滤后集合
     */
    public static List<String> filterEmptyToList(final String[] strs) {
        if(null == strs || 0 == strs.length) {
            return Collections.emptyList();
        }
        return Arrays.stream(strs)
                .filter(str -> !StringUtil.isEmpty(str))
                .distinct()
                .collect(Collectors.toList());
    }

    public static String[] getDiff(final String[] source, final String[] target) {
        if(0 == target.length) {
            return source;
        }
        return getDiff(Arrays.stream(source), Arrays.stream(target));
    }

    public static String[] getDiff(final String[] source, final List<String> target) {
        if(0 == target.size()) {
            return source;
        }
        return getDiff(Arrays.stream(source), target.stream());
    }

    private static String[] getDiff(final Stream<String> source, final Stream<String> target) {
        String[] sourceArr = source
                .sorted(Comparator.comparingInt(String::hashCode))
                .toArray(String[]::new);
        String[] targetArr = target
                .sorted(Comparator.comparingInt(String::hashCode))
                .toArray(String[]::new);
        int sLength = sourceArr.length;
        int tLength = targetArr.length;
        String s, t, ss, tt;
        int jj;
        for (int i=0, j=0; i < sLength; i++, j++) {
            if(tLength <= j) {
                break;
            }
            s = sourceArr[i];
            t = targetArr[j];
            if(s.equals(t)) {
                sourceArr[i] = null;
            }
            else if(s.hashCode() < t.hashCode()) {
                j--;
            }
            else if(s.hashCode() > t.hashCode()) {
                for (jj=j+1; jj < tLength; jj++) {
                    ss = sourceArr[i];
                    tt = targetArr[jj];
                    if(ss.equals(tt)) {
                        sourceArr[i] = null;
                        break;
                    }
                    if(ss.hashCode() < tt.hashCode()) {
                        break;
                    }
                }
            }
        }
        return Arrays.stream(sourceArr)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
