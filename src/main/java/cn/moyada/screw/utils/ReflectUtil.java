package cn.moyada.screw.utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-02-25 15:33
 */
public final class ReflectUtil {

    private static Map<String, SoftReference<Class<?>>> classMap = new HashMap<>();
    private static Map<String, SoftReference<Method>> methodMap = new HashMap<>();

    private static <T> T getValue(Map<String, SoftReference<T>> map, String key) {
        SoftReference<T> ref = map.get(key);
        if(null != ref) {
            T val = ref.get();
            if(null != val) {
                return val;
            }
        }
        return null;
    }

    public static Class<?> getClass(String classPath) {
        if(StringUtil.isEmpty(classPath)) {
            throw new IllegalArgumentException("classPath can not be null.");
        }

        Class<?> findClass = getValue(classMap, classPath);
        if(null != findClass) {
            return findClass;
        }

        try {
            findClass = Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            return null;
        }

        classMap.put(classPath, new SoftReference<>(findClass));
        return findClass;
    }

    public static Method getMethod(Class<?> classType, String methodName, Class<?>... parameterTypes) {
        if(null == classType) {
            throw new NullPointerException("classType can not be null.");
        }
        if(StringUtil.isEmpty(methodName)) {
            throw new IllegalArgumentException("methodName can not be null.");
        }

        String methodKey = classType.getSimpleName() + methodName;
        if(null != parameterTypes && parameterTypes.length > 0) {
            for (Class<?> paramType : parameterTypes) {
                methodKey += paramType.getSimpleName();
            }
        }

        Method method = getValue(methodMap, methodKey);
        if(null != method) {
            return method;
        }

        try {
            method = classType.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }

        methodMap.put(methodKey, new SoftReference<>(method));
        return method;
    }

    public static Method getMethod(String classPath, String methodName, Class<?>... parameterTypes) {
        Class<?> aClass = getClass(classPath);
        if(null == aClass) {
            try {
                throw new ClassNotFoundException("not found " + classPath + " class.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return getMethod(aClass, methodName, parameterTypes);
    }
}
