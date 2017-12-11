package cn.xyk.screw.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by xueyikang on 2017/2/17.
 */
@SuppressWarnings("unchecked")
public class ObjectUtil {


    public static <C> Map<String, Object> getValues(List<C> list, Class<C> cClass) {
        return getValues(list, cClass, 16);
    }

    public static <C> Map<String, Object> getValues(List<C> list, Class<C> cClass, int size) {
        size = size < 16 ? 16 : size;
        Map<String, Object> valueMap = Maps.newHashMapWithExpectedSize(Math.multiplyExact(size, list.size()));
        try {
            for (int index = 0; index < list.size(); index++) {
                valueMap.putAll(getValue(list.get(index), cClass, size, index));
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            return Collections.emptyMap();
        }
        return valueMap;
    }

    private static <C> Map<String, Object> getValue(Object obj, Class<C> c, Integer size, int index) throws IllegalAccessException, ClassNotFoundException {
        if(null == obj) {
            return Collections.emptyMap();
        }
        Field[] fields = c.getDeclaredFields();
        if(null == size) {
            size = null == fields ? 16 : fields.length;
        }
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(size);
        // 获取继承类中属性
        Class s = c.getSuperclass();
        if(null != s) {
            paramMap.putAll(getValue(obj, s, null, index));
        }
        if(null == fields) {
            return paramMap;
        }
        Type type;
        for (Field field: fields) {
            type = field.getGenericType();
            field.setAccessible(true);
            if(type.getTypeName().startsWith("java")) {
                paramMap.put(field.getName().concat("_").concat(String.valueOf(index)) ,field.get(obj));
            }
            else {
                paramMap.putAll(getValue(field.get(obj), Class.forName(type.getTypeName()), null, index));
            }
        }
        return paramMap;
    }

    /**
     * 获取类所有属性集合
     */
    public static Map<String, Object> getValues(Object obj) {
        return getValues(obj, obj.getClass(), 16);
    }

    public static Map<String, Object> getValues(Object obj, int size) {
        return getValues(obj, obj.getClass(), size);
    }

    public static <C> Map<String, Object> getValues(Object obj, Class<C> cClass) {
        return getValues(obj, cClass, 16);
    }

    public static <C> Map<String, Object> getValues(Object obj, Class<C> cClass, int size) {
        try {
            return getValue(obj, cClass, size);
        } catch (IllegalAccessException | ClassNotFoundException e) {
            return Collections.emptyMap();
        }
    }

    private static <C> Map<String, Object> getValue(Object obj, Class<C> c, Integer size) throws IllegalAccessException, ClassNotFoundException {
        if(null == obj) {
            return Collections.emptyMap();
        }
        Field[] fields = c.getDeclaredFields();
        if(null == size) {
            size = null == fields ? 16 : fields.length;
        }
        Map<String, Object> paramMap = Maps.newHashMapWithExpectedSize(size);
        // 获取继承类中属性
        Class s = c.getSuperclass();
        if(null != s) {
            paramMap.putAll(getValue(obj, s, null));
        }
        if(null == fields) {
            return paramMap;
        }
        Type type;
        for (Field field: fields) {
            type = field.getGenericType();
            field.setAccessible(true);
            if(type.getTypeName().startsWith("java")) {
                paramMap.put(field.getName() ,field.get(obj));
            }
            else {
                paramMap.putAll(getValue(field.get(obj), Class.forName(type.getTypeName()), null));
            }
        }
        return paramMap;
    }

    /**
     * 过滤对象之间相同的属性为null
     * @param cClass
     * @param objs
     * @param <C>
     */
    public static <C> void filterObject(List<C> objs, Class<C> cClass) {
        if(objs.size() < 2) {
            return;
        }
        Field[] fields = cClass.getDeclaredFields();
        if(null == fields || 0 == fields.length) {
            return;
        }
        try {
            Object[] param = new Object[objs.size()];
            boolean result;
            for (Field field : fields) {
                if(field.getType().getTypeName().startsWith("java")) {
                    continue;
                }
                field.setAccessible(true);
                result = true;
                for(int i = 0; result && i< objs.size(); i++) {
                    param[i] = field.get(objs.get(i));
                    result = null != param[i];
                }
                if(result) {
                    result = filterParameter(field.getType(), param);
                    if (result) {
                        for (Object obj : objs) {
                            field.set(obj, null);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 过滤对象之间相同的属性为null
     * @param cClass
     * @param objs
     * @param <C>
     * @return
     * @throws IllegalAccessException
     */
    private static <C> boolean filterParameter(Class<C> cClass, Object... objs) throws IllegalAccessException {
        int length = objs.length;
        if(length < 2) {
            return true;
        }
        Field[] fields = cClass.getDeclaredFields();
        if(null == fields || 0 == fields.length) {
            return true;
        }
        int count = 0;
        List<Object> temp = Lists.newArrayListWithExpectedSize(objs.length);
        Object item;
        int nullItemSize;
        boolean noEquals;
        for (Field field : fields) {
            field.setAccessible(true);
            nullItemSize = 0;
            noEquals = false;
            for (Object obj : objs) {
                item = field.get(obj);
                if(null == item) {
                    nullItemSize++;
//                    continue;
                }
                //  存在null与非null
                else if(nullItemSize != 0){
                    noEquals = true;
                    break;
                } else {
                    temp.add(item);
                }
            }
            if(noEquals) {
                temp.clear();
                continue;
            }
            // 全部为null
            if(nullItemSize == length) {
                count++;
            }
            else {
                temp.sort((s1, s2) -> s1.toString().compareTo(s2.toString()));
                if (Objects.equals(temp.get(0), temp.get(temp.size() - 1))) {
                    for (Object obj : objs) {
                        field.set(obj, null);
                    }
                    count++;
                }
            }
            temp.clear();
        }
        return count == length;
    }
}
