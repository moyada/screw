package cn.moyada.screw.cache;

/**
 * @author xueyikang
 * @create 2018-03-14 20:40
 */
public abstract class AbstractCombineRequest {

    abstract void waitIfRunning(String key);

    abstract void notifyWait(String key);
}
