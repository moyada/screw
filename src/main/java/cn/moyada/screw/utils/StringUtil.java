package cn.moyada.screw.utils;


import cn.moyada.screw.cache.StringBuilderPool;
import com.google.common.collect.Lists;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

import java.util.*;

/**
 * Created by xueyikang on 2017/2/23.
 */
public class StringUtil {

    private List<StringBuilder> sbPool = new ArrayList<>();

    private static final String EMPTY = "";
    private static final String NULL = "_";

    public static final boolean isEmpty(String str) {
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

    public static final <T> boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static final <T> boolean isEmpty(T[] objs) {
        return null == objs || objs.length == 0;
    }

    public static final String concat(final Object... objs) {
        if(null == objs) {
            return NULL;
        }

        int length = objs.length;
        if(0 == length) {
            return EMPTY;
        }

        long objectSize = ObjectSizeCalculator.getObjectSize(objs);

        StringBuilder sb = StringBuilderPool.get(Long.valueOf(objectSize).intValue());
//        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < length; index++) {
            appendToOwn(sb, objs[index]);
        }
        String intern = sb.toString().intern();
        StringBuilderPool.add(sb);
        return intern;
    }

    public static void main(String[] args) {
        Object[] objs = new Object[9];
        objs[0] = 123;
        objs[1] = 1255L;
        objs[2] = "haha";
        objs[3] = 'z';
        objs[4] = "6666";
        objs[5] = -32.12d;
        objs[6] = -32.12d;

        long objectSize = ObjectSizeCalculator.getObjectSize(objs);

        System.out.println(objectSize);
        System.out.println(objectSize / 8);
    }

    private static final void appendToOwn(final StringBuilder own, final Object obj) {
        if(null == obj) {
            own.append(NULL);
            return;
        }

        if(obj instanceof Collection) {
            appendToOwn(own, Collection.class.cast(obj));
            return;
        }

        Class<?> objClass = obj.getClass();
        if(objClass.isArray()) {
            Class<?> componentType = objClass.getComponentType();
            if(componentType.isPrimitive()) {
                if(componentType == int.class) {
                    appendToOwn(own, int[].class.cast(obj));
                }
                if(componentType == int.class) {
                    appendToOwn(own, int[].class.cast(obj));
                }
                if(componentType == long.class) {
                    appendToOwn(own, long[].class.cast(obj));
                }
                if(componentType == char.class) {
                    appendToOwn(own, char[].class.cast(obj));
                }
                if(componentType == double.class) {
                    appendToOwn(own, double[].class.cast(obj));
                }
                if(componentType == float.class) {
                    appendToOwn(own, float[].class.cast(obj));
                }
                if(componentType == byte.class) {
                    appendToOwn(own, byte[].class.cast(obj));
                }
                if(componentType == boolean.class) {
                    appendToOwn(own, boolean[].class.cast(obj));
                }
                own.append(NULL);
            }
            else {
                appendToOwn(own, Object[].class.cast(obj));
            }
            return;
        }

        Package objClassPackage = objClass.getPackage();
        if(null != objClassPackage && objClassPackage.getName().equals("java.lang")) {
            own.append(obj.toString().intern());
            return;
        }

        if(obj instanceof Map) {
            appendToOwn(own, Map.class.cast(obj));
            return;
        }

        own.append(obj.toString().intern());
    }

    private static final void appendToOwn(final StringBuilder own, final Collection<?> list) {
        if(null == list) {
            own.append(NULL);
            return;
        }
        if(0 == list.size()) {
            own.append(EMPTY);
            return;
        }

        for (Iterator<?> iterator = list.iterator(); iterator.hasNext(); ) {
            appendToOwn(own, iterator.next());
        }
    }

    private static final void appendToOwn(final StringBuilder own, final Map<?, ?> map) {
        if(null == map) {
            own.append(NULL);
            return;
        }
        if(0 == map.size()) {
            own.append(EMPTY);
            return;
        }

        for(Map.Entry<?, ?> entry : map.entrySet()) {
            own.append(entry.getKey());
            appendToOwn(own, entry.getValue());
        }
    }

    private static final void appendToOwn(final StringBuilder own, final Object[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }

        for (int index = 0; index < length; index++) {
            appendToOwn(own, arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final byte[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final boolean[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final int[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final short[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final long[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final char[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final float[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }

    private static final void appendToOwn(final StringBuilder own, final double[] arr) {
        if(null == arr) {
            own.append(NULL);
            return;
        }
        int length = arr.length;
        if(0 == length) {
            own.append(EMPTY);
            return;
        }
        for (int index = 0; index < length; index++) {
            own.append(arr[index]);
        }
    }
}
