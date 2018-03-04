package cn.moyada.screw.common;

import java.util.function.BiConsumer;

/**
 * @author xueyikang
 * @create 2018-03-04 14:08
 */
public interface AbstractMap<K, V> {

    V put(K key, V val);

    V remove(K key);

    V get(K key);

    void forEach(BiConsumer<? super K, ? super V> consumer);

    int size();

    boolean isEmpty();
}
