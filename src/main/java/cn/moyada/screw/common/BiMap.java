package cn.moyada.screw.common;

import sun.jvm.hotspot.runtime.ConstructionException;

import java.util.function.BiConsumer;

/**
 * @author xueyikang
 * @create 2018-03-02 14:26
 */
public class BiMap<K, V> {

    private static final int DEFAULT_CAPACITY = 15;

    private static final float DEFAULT_LOAD_FACTOR = 0.6f;

    private boolean stw = false;

    private Node<V, K>[] keys;
    private Node<K, V>[] values;

    private Index start, end;

    private int size, capacity;

    public BiMap() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public BiMap(int capacity) {
        if(capacity < 1) {
            throw new ConstructionException("wrong capacity");
        }
        this.capacity = getCap(capacity);
        this.size = 0;
        this.keys = new Node[this.capacity];
        this.values = new Node[this.capacity];
        this.start = new Index(-1);
        this.end = start;
    }

    private int getCap(int size) {
        int bit = 1;
        for(; 1 != size; bit++) {
            size = size >> 1;
        }
        return (size << bit) - 1;
    }

    public void put(K key, V value) {
        while (stw) {
        }

        V exist = putKey(key, value);
        if(null != exist) {
            removeKey(exist);
        }
        putValue(value, key);
    }

    private void removeKey(V value) {
        Node<V, K> node;
        for (int index = hash(value); index < capacity ; index = nextIndex(index)) {
            node = keys[index];
            if(null == node) {
                break;
            }

            if (node.isKey(value)) {
                keys[index] = null;
                break;
            }
        }
    }

    private V putKey(K key, V value) {
        Node<K, V> node;
        for (int index = hash(key); ; index = nextIndex(index)) {
            node = values[index];
            if (null == node) {
                if(size > capacity * DEFAULT_LOAD_FACTOR) {
                    growSize();
                    index--;
                    continue;
                }

                values[index] = new Node<>(key, value);
                end.next = new Index(index);
                end = end.next;
                size++;
                return null;
            }

            if (node.isKey(key)) {
                V result = node.value;
                node.value = value;
                values[index] = node;
                return result;
            }
        }
    }

    private void putValue(V key, K value) {
        Node<V, K> node;
        for (int index = hash(key); ; index = nextIndex(index)) {

            node = keys[index];
            if (null == node) {
                keys[index] = new Node<>(key, value);
                break;
            }

            if (node.isKey(key)) {
                throw new RuntimeException("put ["+ key + ": " + value + "] error, have same value in multiple key.");
            }
        }
    }

    private <KK, VV> void put(KK key, VV value, Node<KK, VV>[] arr, int capacity, boolean countSize) {
        Node<KK, VV> node;
        for (int index = hash(key, capacity); ; index = nextIndex(index)) {
            node = arr[index];
            if (null == node) {
                arr[index] = new Node<>(key, value);
                if(countSize) {
                    end.next = new Index(index);
                    end = end.next;
                }
                break;
            }
        }
    }

    private void growSize() {
        this.stw = true;
        int newCap = ((capacity + 1) << 1) - 1;

        rehash(newCap);

        this.capacity = newCap;
        this.stw = false;
    }

    @SuppressWarnings("unchecked")
    private void rehash(int capacity) {
        Node<V, K>[] newKeys = new Node[capacity];
        Node<K, V>[] newVals = new Node[capacity];

        this.end = this.start;
        Node<K, V> node;
        Index next = this.start.next;

        for (; null != next; next = next.next) {
            if(null != (node = this.values[next.index])) {
                put(node.key, node.value, newVals, capacity, true);
                put(node.value, node.key, newKeys, capacity, false);
            }
        }

        this.keys = newKeys;
        this.values = newVals;
    }

    public V get(K key) {
        return getVal(key, values);
    }

    public K getKey(V value) {
        return getVal(value, keys);
    }

    private <KK, VV> VV getVal(KK key, Node<KK, VV>[] values) {
        Node<KK, VV> node;

        for (int index = hash(key); index < capacity; index = nextIndex(index)) {
            node = values[index];
            if(null == node) {
                return null;
            }

            if (node.isKey(key)) {
                return node.value;
            }
        }
        return null;
    }

    public void forEach(BiConsumer<? super K, ? super V> var1) {
        if (var1 == null) {
            throw new NullPointerException();
        } else {
            Node<K, V> node;
            Index next = start.next;
            for (; null != next; next = next.next) {
                if(null != (node = values[next.index])) {
                    var1.accept(node.key, node.value);
                }
            }
        }
    }

    public int size() {
        return size;
    }

    private int nextIndex(int index) {
        return (index + 1) & capacity;
    }

    private <T> int hash(T obj) {
        return hash(obj, this.capacity);
    }

    private <T> int hash(T obj, int capacity) {
        return (obj.hashCode() & capacity + capacity) & capacity;
    }

    private class Index {
        private int index;
        private Index next;

        public Index(int index) {
            this.index = index;
        }
    }

    private final class Node<X, Y> extends Padding {
        private volatile X key;
        private volatile Y value;

        public Node(X key, Y value) {
            this.key = key;
            this.value = value;
        }

        boolean isKey(X key) {
            return this.key.equals(key);
        }
    }

    private class Padding {
        private long p1, p2, p3, p4, p5;

        private long getP() {
            return p1 + p2 + p3 + p4 + p5;
        }
    }
}