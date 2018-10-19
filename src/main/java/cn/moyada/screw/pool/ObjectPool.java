package cn.moyada.screw.pool;

import java.util.function.Supplier;

/**
 * 对象资源池
 * @author xueyikang
 * @create 2018-05-29 19:53
 */
public interface ObjectPool<T> {

    /**
     * 获取资源，资源不存在则使用默认方式创建
     * @return
     */
    T allocate();

    /**
     * 获取资源，资源不存在则使用指定方式创建
     * @param initCommand 创建方式
     * @return
     */
    T allocate(Supplier<T> initCommand);

    /**
     * 回收资源
     * @param obj
     */
    void recycle(T obj);
}
