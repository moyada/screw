package cn.moyada.screw.pool;

import java.util.function.Supplier;

/**
 * @author xueyikang
 * @create 2018-06-15 19:52
 */
public abstract class AbstractBeanPool<T> implements BeanPool<T> {

    /**
     * 最大可容纳数
     */
    protected final int maxCapacity;

    /**
     * 新对象生成器
     */
    protected Supplier<T> defaultBean;

    public AbstractBeanPool(int maxCapacity, Supplier<T> defaultBean) {
        this.maxCapacity = maxCapacity;
        this.defaultBean = defaultBean;
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
