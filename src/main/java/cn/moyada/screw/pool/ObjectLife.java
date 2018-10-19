package cn.moyada.screw.pool;


/**
 * 对象生命周期
 */
public interface ObjectLife {

    /**
     * 初始化执行方法
     */
    void init();

    /**
     * 回收执行方法
     */
    void destroy();
}
