package cn.moyada.screw.lock;

/**
 * 分布式锁
 * @author xueyikang
 * @create 2018-06-05 02:27
 */
public interface DistributionLock {

    /**
     * 上锁
     * @param key
     * @return
     */
    boolean tryLock(String key);

    /**
     * 解锁
     * @param key
     * @return
     */
    boolean release(String key);
}
