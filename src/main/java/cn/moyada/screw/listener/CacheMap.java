package cn.moyada.screw.listener;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xueyikang
 * @create 2018-03-16 00:02
 */
public class CacheMap<V> {

    private static final int DEFAULT_TIME = 10;

    private Map<String, Content<V>> cacheMap;

    public CacheMap() {
        this(TimeUnit.SECONDS);
    }

    public CacheMap(TimeUnit timeUnit) {
        this.cacheMap = new ConcurrentHashMap<>();
    }

    public void put(String k, V v) {
        put(k, v, DEFAULT_TIME);
    }

    public void put(String k, V v, int time) {
        cacheMap.put(k, new Content<>(v));
    }

    public V get(String k) {
        Content<V> content = cacheMap.get(k);
        if(null == content) {
            return null;
        }
        return content.v;
    }

    class Content<V> {
        private V v;

        Content(V v) {
            this.v = v;
        }
    }

    class TimeoutListener {

        private static final float LOAD_FACTOR = 0.7f;

        private int threshold;

        private Queue<Node> queue;

        private Thread listener;

        private Lock lock;

        public TimeoutListener() {
            int initSize = 200;
            queue = new PriorityQueue<>(initSize);
            threshold = Float.valueOf(initSize * LOAD_FACTOR).intValue();
            lock = new ReentrantLock();

            listener = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if(queue.isEmpty()) {
                            LockSupport.park();
                        }


                    }
                }
            });
        }

        public void add(String key, int time) {

        }
    }

    private class Node {

        private int time;

        private String key;

        Node(int time, String key) {
            this.time = time;
            this.key = key;
        }
    }
}
