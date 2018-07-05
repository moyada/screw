package cn.moyada.screw.collection;

import sun.jvm.hotspot.runtime.ConstructionException;
import sun.misc.Contended;

import java.io.Serializable;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

/**
 * @author xueyikang
 * @create 2018-03-01 21:28
 */
public class EnumMap<K extends Enum, V> implements AbstractMap<K, V>, Serializable {
    private static final long serialVersionUID = -6399432773280418371L;

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

    @Override
    public V put(K key, V val) {
        int index = index(key);
        Node<V> value = values[index];
        values[index] = new Node<>(val);
        if(null == value) {
            size.increment();
            return null;
        }
        return value.value;
    }

    @Override
    public V remove(K key) {
        int index = index(key);
        Node<V> value = values[index];
        values[index] = null;
        if(null == value) {
            return null;
        }
        size.decrement();
        return value.value;
    }

    @Override
    public V get(K key) {
        int index = index(key);
        Node<V> node = values[index];
        if(null == node) {
            return null;
        }
        return node.value;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        if (consumer == null) {
            throw new NullPointerException();
        } else {
            int length = values.length;
            Node<V> value;
            for (int i = 0; i < length; i++) {
                if(null != (value = values[i])) {
                    consumer.accept(keys[i], value.value);
                }
            }
        }
    }

    @Override
    public int size() {
        return size.intValue();
    }

    @Override
    public boolean isEmpty() {
        return size.intValue() == 0;
    }

    private int index(K key) {
        return key.ordinal();
    }

    @Contended
    protected final class Node<T> extends Padding {

        private final T value;

        public Node(T value) {
            this.value = value;
        }
    }

    private abstract class Padding {
        private long p1, p2, p3, p4, p5, p6, p7;

        public long getP() {
            return p1 + p2 + p3 + p4 + p5 + p6 + p7;
        }
    }
}