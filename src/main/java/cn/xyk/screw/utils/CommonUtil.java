package cn.xyk.screw.utils;


import net.sf.cglib.beans.BeanCopier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xueyikang on 2016/11/22.
 */
public class CommonUtil {

    /**
     * 过滤空元素
     *
     * @param t   对象数组
     * @return 过滤后数组
     */
    public static String[] filterEmpty(final String[] t) {
        if(null == t || 0 == t.length) {
            return t;
        }
        return Arrays.stream(t)
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

    public static String[] getMapKey(final Map<String, ?> map) {
        return map.keySet().stream().toArray(String[]::new);
    }

    public static String[] getMapValue(final Map<?, String> map) {
        return map.values().stream().toArray(String[]::new);
    }

    public static String[] getDiff(final String[] source, final String[] target) {
        if(0 == target.length) {
            return source;
        }
        String[] sourceArr = Arrays.stream(source)
                .sorted((s1, s2) -> Integer.compare(s1.hashCode(), s2.hashCode()))
                .toArray(String[]::new);
        String[] targetArr = Arrays.stream(target)
                .sorted((s1, s2) -> Integer.compare(s1.hashCode(), s2.hashCode()))
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
                .filter(code -> null != code)
                .toArray(String[]::new);
    }

    public static String[] getDiff(final String[] source, final List<String> target) {
        if(0 == target.size()) {
            return source;
        }
        String[] sourceArr = Arrays.stream(source)
                .sorted((s1, s2) -> Integer.compare(s1.hashCode(), s2.hashCode()))
                .toArray(String[]::new);
        String[] targetArr = target.stream()
                .sorted((s1, s2) -> Integer.compare(s1.hashCode(), s2.hashCode()))
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
                .filter(code -> null != code)
                .toArray(String[]::new);
    }

    /**
     * 复制对象为指定类型对象，对同名称同类型属性进行复制
     *
     * @param source 源对象
     * @param target 目标对象
     * @return 目标Class类对象
     */
    public static void copyToObject(final Object source, final Object target) {
        BeanCopier.create(source.getClass(), target.getClass(), false).copy(source, target, null);
    }

    /**
     * 复制对象为指定类型对象，对同名称同类型属性进行复制
     *
     * @param source 源对象
     * @param target 目标Class类
     * @param <T>    源class
     * @param <C>    目标class
     * @return 目标Class类对象
     */
    public static <T, C> C copyToObject(final T source, final Class<C> target) {
        C obj;
        try {
            obj = target.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
        BeanCopier.create(source.getClass(), target, false).copy(source, obj, null);
        return obj;
    }

    /**
     * 复制list集合到指定对象list，对同名称同类型属性进行复制
     *
     * @param sourceList 源list
     * @param targetList 目标Class类
     * @param <T>    源class
     * @param <C>    目标class
     * @return 目标Class类list集合
     */
    public static <T, C> List<C> copyToList(final List<T> sourceList, final Class<C> targetList) {
        if(null == sourceList || 0 == sourceList.size()) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(t -> copyToObject(t, targetList))
                .filter(t -> null != t)
                .collect(Collectors.toList());
    }
}
