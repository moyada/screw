package cn.moyada.screw.pool;

import java.util.function.Supplier;

/**
 * 单线程资源池
 * @author xueyikang
 * @create 2018-05-29 19:21
 */
public class SingleObjectPool<T> extends AbstractObjectPool<T> {

    protected int size;

    /**
     * 头指针
     */
    protected Item first;

    /**
     * 尾指针
     */
    protected Item last;

    SingleObjectPool(int size, Supplier<T> defaultSupplier) {
        super(size, defaultSupplier);
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
        return allocate(defaultSupplier);
    }

    @Override
    public T allocate(Supplier<T> supplier) {
        if(isEmpty()) {
            return initObject(supplier);
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
    public void recycle(T obj) {
        clear(obj);
        if(null == last) {
            init(obj);
            return;
        }
        if(size >= maxCapacity) {
            return;
        }
        final Item item = new Item(obj);
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
