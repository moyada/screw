package cn.moyada.screw.pool;

import java.util.function.Supplier;

/**
 * @author xueyikang
 * @create 2018-06-15 19:52
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {

    /**
     * 最大可容纳数
     */
    protected final int maxCapacity;

    /**
     * 新对象生成器
     */
    protected Supplier<T> defaultSupplier;

    public AbstractObjectPool(int maxCapacity, Supplier<T> defaultSupplier) {
        this.maxCapacity = maxCapacity;
        this.defaultSupplier = defaultSupplier;
    }

    /**
     * 初始化对象
     * @param supplier
     * @return
     */
    protected T initObject(Supplier<T> supplier) {
        T obj = supplier.get();
        if (obj instanceof ObjectLife) {
            ((ObjectLife) obj).init();
        }
        return obj;
    }

    /**
     * 清理对象
     * @param obj
     */
    protected void clear(T obj) {
        if (obj instanceof ObjectLife) {
            ((ObjectLife) obj).destroy();
        }
    }

    /**
     * 判断资源池中是否有数据
     * @return
     */
    protected abstract boolean isEmpty();

    /**
     * 对象资源
     * @param
     */
    class Item {

        Item(T value) {
            this.value = value;
            this.next = null;
        }

        T value;

        Item next;
    }
}
