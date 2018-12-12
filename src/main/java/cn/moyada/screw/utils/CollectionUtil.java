package cn.moyada.screw.utils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author xueyikang
 * @create 2018-03-12 14:12
 */
public interface CollectionUtil {

    public static boolean isEmpty(Collection list) {
        return null == list || list.size() == 0;
    }

    public static boolean isNotEmpty(Collection list) {
        return !isEmpty(list);
    }

    public static boolean isEmpty(Map list) {
        return null == list || list.size() == 0;
    }

    public static boolean isNotEmpty(Map list) {
        return !isEmpty(list);
    }

    public static <T> Collection<T> combine(Collection<T> set1, Collection<T> set2) {
        boolean set1empty = null == set1 || set1.size() == 0;
        boolean set2empty = null == set2 || set2.size() == 0;
        if(set1empty) {
            if(set2empty) {
                return Collections.emptySet();
            }
            return set2;
        }
        if(set2empty) {
            return set1;
        }

        Set<T> newSet = new HashSet<>(set1.size() + set2.size());
        newSet.addAll(set1);
        newSet.addAll(set2);
        return newSet;
    }

    public static <E> LinkedList<E> reverse(LinkedList<E> source) {
        LinkedList<E> reverse = new LinkedList<>();
        E value;
        while (null != (value = source.pollLast())) {
            reverse.offer(value);
        }

        return reverse;
    }

    public static <T> T[] getMapKey(final Map<T, ?> map, Class<T> keyClass) {
        return getValue(map.keySet().stream(), keyClass);
    }

    public static <T> T[] getMapValue(final Map<?, T> map, Class<T> valueClass) {
        return getValue(map.values().stream(), valueClass);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] getValue(Stream<T> stream, Class<T> clazz) {
        return stream.toArray(size -> (T[]) Array.newInstance(clazz, size));
    }
}

