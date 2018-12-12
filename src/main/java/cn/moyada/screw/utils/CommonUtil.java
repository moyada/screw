package cn.moyada.screw.utils;


import org.jboss.netty.handler.codec.serialization.SoftReferenceMap;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xueyikang on 2016/11/22.
 */
public final class CommonUtil {

    private static final Unsafe THE_UNSAFE;

    private static final Map<String, Long> offsetMap = new SoftReferenceMap<>(new ConcurrentHashMap<>());
    private static final Map<String, Field> fieldMap = new SoftReferenceMap<>(new ConcurrentHashMap<>());

    static
    {
        try
        {
            final PrivilegedExceptionAction<Unsafe> action = () -> {
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                return (Unsafe) theUnsafe.get(null);
            };
            THE_UNSAFE = AccessController.doPrivileged(action);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load unsafe", e);
        }
    }

    /**
     * Get a handle on the Unsafe instance, used for accessing low-level concurrency
     * and memory constructs.
     *
     * @return The Unsafe
     */
    public static Unsafe getUnsafe() {
        return THE_UNSAFE;
    }

    private static long getOffset(Class clazz, Field field) {
        String key = ConcatUtil.concat(clazz.getName() + field.getName());
        Long offset = offsetMap.get(key);
        if(null == offset) {
            offset = THE_UNSAFE.objectFieldOffset(field);
            offsetMap.put(key, offset);
        }
        return offset;
    }

    private static Field getField(Class clazz, String fieldName) {
        String key = ConcatUtil.concat(clazz.getName() + fieldName);
        Field field = fieldMap.get(key);
        if(null == field) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return null;
            }
            fieldMap.put(key, field);
        }
        return field;
    }

    public static boolean compareAndSwapParameter(Object object, String paramName,
                                               Object oldValue, Object newValue) {
        return compareAndSwapParameter(object, paramName, oldValue, newValue, object.getClass());
    }

    public static boolean compareAndSwapParameter(Object object, String paramName,
                                               Object oldValue, Object newValue, Class clazz) {
        Field field = getField(clazz, paramName);
        if(null == field) {
            return false;
        }

        long offset = getOffset(clazz, field);

        boolean success;
        Object value = THE_UNSAFE.getObjectVolatile(object, offset);
        if(!value.toString().equals(oldValue.toString())) {
            return false;
        }

        switch (field.getDeclaringClass().getSimpleName()) {
            case "int":
            case "Integer":
                success = THE_UNSAFE.compareAndSwapInt(object, offset, (Integer) value, (Integer) newValue);
                break;
            case "long":
            case "Long":
                success = THE_UNSAFE.compareAndSwapLong(object, offset, (Long) value, (Long) newValue);
                break;
            default:
                success = THE_UNSAFE.compareAndSwapObject(object, offset, value, newValue);
                break;
        }
        return success;
    }

    public static String getRealPath(String fileName) {
        URL resource = CommonUtil.class.getResource("/" + fileName);
        if(null != resource) {
            return resource.getPath();
        }

        if(Files.exists(Paths.get(fileName))) {
            return fileName;
        }

        resource = CommonUtil.class.getResource(fileName);
        if(null != resource) {
            return resource.getPath();
        }
        return null;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
