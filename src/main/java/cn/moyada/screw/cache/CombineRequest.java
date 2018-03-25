package cn.moyada.screw.cache;

/**
 * @author xueyikang
 * @create 2018-03-14 20:40
 */
public abstract class CombineRequest {

    // 队列大小
    protected static final int LIMIT = (1 << 10) - 1;

    public abstract void waitIfRunning(String key);

    public abstract void notifyWait(String key);
}
