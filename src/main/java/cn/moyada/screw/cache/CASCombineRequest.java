package cn.moyada.screw.cache;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;

/**
 * 针对大请求同一方法防止请求穿透
 *
 * Demo
 * --------
 *
 * public String getCache(String key) {
 *     String value = cacheService.get(key);
 *     if(null != value) {
 *         return value;
 *     }
 *
 *     CASCombineRequest.waitIfRunning(key);
 *
 *     return cacheService.get(key);
 * }
 *
 *  public void setCache(String key, String value) {
 *     cacheService.set(key, value);
 *
 *     CASCombineRequest.notifyWait(key);
 * }
 *
 * --------
 *
 * @author xueyikang
 * @create 2018-03-09 21:33
 */
public class CASCombineRequest extends CombineRequest {

    // 阻塞队列
    private final Map<Integer, Queue<Thread>> blockingMap;

    // 队列下标
    private final AtomicReferenceArray<String> indexQueue;
    // 队列标记
    private final AtomicReferenceArray<Boolean> queueMark;

    // 运行标记
    private final AtomicReferenceArray<Boolean> runningMark;

    public CASCombineRequest() {
        blockingMap = new ConcurrentHashMap<>(LIMIT);
        indexQueue = new AtomicReferenceArray<>(LIMIT);
        queueMark = new AtomicReferenceArray<>(LIMIT);
        runningMark = new AtomicReferenceArray<>(LIMIT);

        // init false
        for (int i = 0; i < LIMIT; i++) {
            runningMark.set(i, false);
            queueMark.set(i, false);
        }
    }

    private static int getNodeIndex(final String key) {
        return ((key.hashCode() & LIMIT) + key.length()) & LIMIT;
    }

    private int getIndex(final String key) {
        int index = getNodeIndex(key);

        for (;;) {
            if(indexQueue.compareAndSet(index, null, key) || indexQueue.compareAndSet(index, key, key)) {
                return index;
            }
            index = (index + 1) & LIMIT;
        }
    }

    private int removeIndex(final String key) {
        int index = getNodeIndex(key);

        for (;;) {
            if(indexQueue.compareAndSet(index, key, null)) {
                return index;
            }
            index = (index + 1) & LIMIT;
        }
    }

    public void waitIfRunning(final String key) {
        int index;

        for (;;) {
            index = getIndex(key);
            if (runningMark.compareAndSet(index, true, true)) {
                Queue<Thread> queue = blockingMap.get(index);
                if(null == queue) {
                    if(queueMark.compareAndSet(index, false, true)) {
                        queue = new LinkedBlockingQueue<>();
                        blockingMap.put(index, queue);
                    }
                    else {
                        do {
                            queue = blockingMap.get(index);
                        }
                        while (null == queue);
                    }
                    queueMark.compareAndSet(index, true, false);
                }

                queue.add(Thread.currentThread());

                // block current thread
                LockSupport.park(key);
                return;
            } else if (runningMark.compareAndSet(index, false, true)) {
                return;
            }
            // update failed
        }
    }

    public void notifyWait(final String key) {
        int index = removeIndex(key);
        // reset running mark
        runningMark.compareAndSet(index, true, false);

        Queue<Thread> blockingDeque = blockingMap.get(index);
        if(null == blockingDeque) {
            return;
        }

        // release blocking thread
        Thread thread;
        while (null != (thread = blockingDeque.poll())) {
            LockSupport.unpark(thread);
        }
    }
}