package cn.moyada.screw.cache;

import cn.moyada.screw.utils.StringUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xueyikang
 * @create 2018-03-18 23:56
 */
public class ZookeeperCombineRequest extends CombineRequest {

    private final CuratorFramework client;
    private final String basePath;

    // 锁标记
    private final Map<String, InterProcessMutex> lockMap;

    // 阻塞队列
    private volatile Map<String, Queue<Thread>> blockingMap;

    public ZookeeperCombineRequest(String connection, String basePath) {
        if(StringUtil.isEmpty(basePath)) {
            throw new IllegalArgumentException("basePath can not be null.");
        }
        if(!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        this.client = CuratorFrameworkFactory.newClient(connection, new ExponentialBackoffRetry(1000, 3));
        this.basePath = basePath + "/";
        this.lockMap = new ConcurrentHashMap<>(LIMIT);
        this.client.start();
        try {
            if(null == this.client.checkExists().forPath(basePath)) {
                this.client.create().forPath(basePath);
            }
            if(null == this.client.checkExists().forPath(basePath + "-q-")) {
                this.client.create().forPath(basePath + "-q-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void waitIfRunning(String key) {
        if (!lock(key)) {
            Queue<Thread> queue = blockingMap.get(key);
            if(null == queue) {
                synchronized (this) {
                    if (null == queue) {
                        queue = new LinkedBlockingQueue<>();
                        blockingMap.put(key, queue);
                    }
                }
            }

            queue.add(Thread.currentThread());

            // block current thread
            LockSupport.park(key);
        }
    }

    private boolean lock(String path) {
        InterProcessMutex lock = new InterProcessMutex(client, basePath.concat(path));
        try {
            if(lock.acquire(100, TimeUnit.MILLISECONDS))
                lockMap.put(path, lock);
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean release(String path) {
        try {
            lockMap.remove(path).release();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void notifyWait(String key) {
        release(key);

        Queue<Thread> blockingDeque = blockingMap.get(key);
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
