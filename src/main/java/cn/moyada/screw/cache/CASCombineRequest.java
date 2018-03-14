package cn.moyada.screw.cache;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;

/**
 * 针对大请求同一方法防止请求穿透
 * @author xueyikang
 * @create 2018-03-09 21:33
 */
public class CASCombineRequest extends AbstractCombineRequest {

    // 阻塞队列
    private final Map<Integer, Queue<Thread>> blockingMap;

    // 队列大小
    private static int LIMIT = (1 << 8) - 1;

    // 队列下标
//    private volatile String[] indexQueue;
    private volatile AtomicReferenceArray<String> indexQueue;
    // 运行标记
    private volatile AtomicReferenceArray<Boolean> queueMark;

    // 运行标记
    private volatile AtomicReferenceArray<Boolean> runningMark;

    public CASCombineRequest() {
        blockingMap = new ConcurrentHashMap<>(LIMIT);
//        indexQueue = new String[LIMIT];
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
                Queue<Thread> deque = blockingMap.get(index);
                if(null == deque) {
                    if(queueMark.compareAndSet(index, false, true)) {
                        deque = new LinkedBlockingQueue<>();
                        blockingMap.put(index, deque);
                    }
                    else {
                        do {
                            deque = blockingMap.get(index);
                        }
                        while (null == deque);
                    }
                    queueMark.compareAndSet(index, true, false);
                }

                deque.add(Thread.currentThread());

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