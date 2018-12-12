package cn.moyada.screw.utils;

import net.sf.cglib.beans.BeanCopier;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class CloneUtil {

    private static Map<Class, BeanCopier> copyMap = new ConcurrentHashMap<>();

    /**
     * 复制list集合到指定对象list，对同名称同类型属性进行复制
     *
     * @param source 源list
     * @param target 目标Class类
     * @param <T>    源class
     * @param <C>    目标class
     * @return 目标Class类list集合
     */
    public static <T, C> Collection<C> copyToList(final Collection<T> source, final Class<C> target) {
        if(null == source || 0 == source.size()) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(t -> copyToObject(t, target))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T, C> C[] copyToArray(final T[] source, final Class<C> target) {
        if(null == source || 0 == source.length) {
            return (C[]) Array.newInstance(target, 0);
        }
        return Arrays.stream(source)
                .map(t -> copyToObject(t, target))
                .filter(Objects::nonNull)
                .toArray(size -> (C[]) Array.newInstance(target, size));
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
        if(null == source) {
            return null;
        }
        C obj;
        try {
            obj = target.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }
        BeanCopier copier = copyMap.get(target);
        if(null == copier) {
            BeanCopier.Generator gen = new BeanCopier.Generator();
            gen.setSource(source.getClass());
            gen.setTarget(target);
            copier = gen.create();
            copyMap.put(target, copier);
        }
        copier.copy(source, obj, null);
        return obj;
    }
}
