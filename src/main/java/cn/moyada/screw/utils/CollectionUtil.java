package cn.moyada.screw.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xueyikang
 * @create 2018-03-12 14:12
 */
public class CollectionUtil {

    public static final boolean isEmpty(Collection list) {
        return null == list || list.size() == 0;
    }

    public static final boolean isNotEmpty(Collection list) {
        return !isEmpty(list);
    }

    public static <T> Set<T> combine(Set<T> set1, Set<T> set2) {
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
}

