package cn.moyada.screw.pool;

import java.util.function.Supplier;

/**
 * 单线程资源池
 * @author xueyikang
 * @create 2018-05-29 19:21
 */
public class SingleBeanPool<T> extends AbstractBeanPool<T> {

    protected int size;

    /**
     * 头指针
     */
    protected Item first;

    /**
     * 尾指针
     */
    protected Item last;

    SingleBeanPool(int size, Supplier<T> defaultBean) {
        super(size, defaultBean);
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    @Override
    protected boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T allocate() {
        return allocate(defaultBean);
    }

    @Override
    public T allocate(Supplier<T> initBean) {
        if(isEmpty()) {
            return initBean.get();
        }

        if(null == first) {
            return null;
        }
        T value = first.value;
        first = first.next;
        if(null == first) {
            last = null;
        }

        size--;
        return value;
    }

    @Override
    public void recycle(T bean) {
        if(null == last) {
            init(bean);
            return;
        }
        if(size >= maxCapacity) {
            return;
        }

        final Item item = new Item(bean);
        last.next = item;
        last = item;
        size++;
    }

    /**
     * 初始化节点
     * @param value
     */
    private void init(T value) {
        final Item item = new Item(value);
        first = item;
        last = item;
    }
}
