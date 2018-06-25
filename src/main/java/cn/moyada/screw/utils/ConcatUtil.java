package cn.moyada.screw.utils;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-06-25 15:41
 */
public class ConcatUtil {

    private static BeanPool<StringBuilder> sbPool = BeanPoolFactory.newPool(32, () -> new StringBuilder(64), false);

    private static final String EMPTY = "";
    private static final String NULL = "_";

    public static String concat(final Object... objs) {
        if(null == objs) {
            return NULL;
        }

        int length = objs.length;
        if(0 == length) {
            return EMPTY;
        }

        StringBuilder sb = sbPool.allocate();

        for (int index = 0; index < length; index++) {
            appendToOwn(sb, objs[index]);
        }
        String concat = sb.toString().intern();

        sb.setLength(0);
        sbPool.recycle(sb);

        return concat;
    }

    private static void appendToOwn(final StringBuilder own, final Object obj) {
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
                if(componentType == short.class) {
                    appendToOwn(own, short[].class.cast(obj));
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

    private static void appendToOwn(final StringBuilder own, final Collection<?> list) {
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

    private static void appendToOwn(final StringBuilder own, final Map<?, ?> map) {
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

    private static void appendToOwn(final StringBuilder own, final Object[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final byte[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final boolean[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final int[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final short[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final long[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final char[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final float[] arr) {
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

    private static void appendToOwn(final StringBuilder own, final double[] arr) {
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
