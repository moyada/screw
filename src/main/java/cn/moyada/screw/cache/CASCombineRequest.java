package cn.moyada.screw.cache;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;

/**
 * 针对大请求同一方法防止请求穿透
 * @author xueyikang
 * @create 2018-03-09 21:33
 */
public class CASCombineRequest extends AbstractCombineRequest {

    // 阻塞队列
    private Map<Integer, Deque<Thread>> blockingMap;

    // 队列大小
    private static int LIMIT = (1 << 8) - 1;

    // 队列下标
//    private volatile String[] indexQueue;
    private volatile AtomicReferenceArray<String> indexQueue;

    // 运行标记
    private volatile AtomicReferenceArray<Boolean> runningMark;

    public CASCombineRequest() {
        blockingMap = new ConcurrentHashMap<>(LIMIT);
//        indexQueue = new String[LIMIT];
        indexQueue = new AtomicReferenceArray<>(LIMIT);
        runningMark = new AtomicReferenceArray<>(LIMIT);

        // init false
        for (int i = 0; i < LIMIT; i++) {
            runningMark.set(i, false);
        }
    }

    private static int getNodeIndex(final String key) {
        int hashCode = key.hashCode();
        return ((hashCode & LIMIT) + LIMIT) & LIMIT;
    }

    private int getIndex(final String key) {
        int index = getNodeIndex(key);

        for (;;) {
            if(indexQueue.compareAndSet(index, null, key) || indexQueue.compareAndSet(index, key, key)) {
                return index;
            }
            index++;
        }
    }

    private int removeIndex(final String key) {
        int index = getNodeIndex(key);

        for (;;) {
            if(indexQueue.compareAndSet(index, key, null)) {
                return index;
            }
            index++;
        }
    }

    public void waitIfRunning(final String key) {
        int index;

        for (;;) {
            index = getIndex(key);
            if (runningMark.compareAndSet(index, true, true)) {

                blockingMap.putIfAbsent(index, new ConcurrentLinkedDeque<>());

                blockingMap.get(index).addLast(Thread.currentThread());

                // block current thread
                LockSupport.park();
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

        Deque<Thread> blockingDeque = blockingMap.get(index);
        if(null == blockingDeque) {
            return;
        }

        // release blocking thread
//        Thread thread;
        for (Thread thread = blockingDeque.pollFirst(); null != thread; thread = blockingDeque.pollFirst()) {
//        while (null != (thread = blockingDeque.pollFirst())) {
            LockSupport.unpark(thread);
        }
    }
}