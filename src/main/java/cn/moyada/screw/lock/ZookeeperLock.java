package cn.moyada.screw.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-06-04 22:19
 */
public class ZookeeperLock implements DistributionLock {

    private final CuratorFramework client;

    public ZookeeperLock(String connection) {
        this.client = CuratorFrameworkFactory
                .builder()
                .connectString(connection)
                .sessionTimeoutMs(3000)
                .connectionTimeoutMs(3000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("lock")
                .build();
        this.client.start();
    }

    public void lock(String key, Runnable command) {
        InterProcessMutex lock = new InterProcessMutex(client, key);
        try {
            if (lock.acquire(3L, TimeUnit.SECONDS) ) {
                try {
                    command.run();
                }
                finally {
                    lock.release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryLock(String key) {
        InterProcessMutex lock = new InterProcessMutex(client, key);
        try {
            return lock.acquire(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean release(String key) {
        InterProcessMutex lock = new InterProcessMutex(client, key);
        try {
            lock.release();
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }
}
