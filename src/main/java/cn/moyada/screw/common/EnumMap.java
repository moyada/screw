package cn.moyada.screw.common;

import sun.jvm.hotspot.runtime.ConstructionException;
import sun.misc.Contended;

import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

/**
 * @author xueyikang
 * @create 2018-03-01 21:28
 */
public class EnumMap<K extends Enum, V> {

    private K[] keys;
    private Node<V>[] values;

    private LongAdder size;

    @SuppressWarnings("unchecked")
    public EnumMap(Class<K> kClass) {
        if(!kClass.isEnum()) {
            throw new ConstructionException("Map key must be Enum Class.");
        }
        keys = kClass.getEnumConstants();
        if(null == keys || keys.length == 0) {
            throw new ConstructionException("Enum Class can not be empty.");
        }
        values = new Node[keys.length];
        size = new LongAdder();
    }

    public void put(K key, V val) {
        int index = key.ordinal();
        if(null == values[index]) {
            size.increment();
        }
        values[index] = new Node<>(val);
    }

    public V get(K key) {
        int index = key.ordinal();
        Node<V> node = values[index];
        if(null == node) {
            return null;
        }
        return node.value;
    }

    public void forEach(BiConsumer<? super K, ? super V> var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            int length = values.length;
            Node<V> value;
            for (int i = 0; i < length; i++) {
                if(null != (value = values[i])) {
                    var1.accept(keys[i], value.value);
                }
            }
        }
    }

    public int size() {
        return size.intValue();
    }

    public boolean isEmpty() {
        return size.intValue() == 0;
    }

    @Contended
    private final class Node<T> { // extends Padding {

        private final T value;

        public Node(T value) {
            this.value = value;
        }
    }

    private abstract class Padding {
        private long p1, p2, p3, p4, p5, p6;

        public long getP() {
            return p1 + p2 + p3 + p4 + p5 + p6;
        }
    }
}