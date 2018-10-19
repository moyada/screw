package cn.moyada.screw.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractGeneric<T> {

    public Class<T> getGenericClass() {
        Type genericType = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
        @SuppressWarnings("unchecked")
        Class<T> genericClass = (Class<T>) types[0];
        return genericClass;
    }
}
