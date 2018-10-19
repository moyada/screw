package cn.moyada.screw.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 并发资源池
 * @author xueyikang
 * @create 2018-05-29 19:49
 */
public class ConcurrentObjectPool<T> extends AbstractObjectPool<T> {

    protected final AtomicInteger size;

    /**
     * 头指针
     */
    protected final AtomicReference<Item> first;

    /**
     * 尾指针
     */
    protected final AtomicReference<Item> last;

    ConcurrentObjectPool(int size, Supplier<T> defaultSupplier) {
        super(size, defaultSupplier);
        this.first = new AtomicReference<>(null);
        this.last = new AtomicReference<>(null);
        this.size = new AtomicInteger(0);
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

        Item item, next;

        // 取出头指针资源，替换为下一个资源
        do {
            item = first.get();
            if(null == item) {
                return initObject(supplier);
            }
            next = item.next;
        }
        while (!first.compareAndSet(item, next));

        // 新资源为空，清空尾指针
        if(null == next) {
            if(!last.compareAndSet(item, null)) {
                // 当回收资源同时发生时
                first.compareAndSet(null, item.next);
            }
        }

        int newSize = size.decrementAndGet();
        if(newSize < 0) {
            size.incrementAndGet();
        }

        return item.value;
    }

    @Override
    protected boolean isEmpty() {
        return size.get() == 0;
    }

    @Override
    public void recycle(T obj) {
        clear(obj);
        if(size.intValue() >= maxCapacity) {
            return;
        }
        final Item next = new Item(obj); // 创建新资源
        Item item = last.get(); // 获取尾指针指向资源

        if(null == item) {
            synchronized (this) {
                // 双重校验
                item = last.get();
                if(null == item) {
                    // 初始化节点
                    first.set(next);
                    last.set(next);
                    return;
                }
            }
        }

        // 设置新尾指针
        while (!last.compareAndSet(item, next)) {
            item = last.get();
        }

        // 设置原尾指针next
        item.next = next;

        size.getAndIncrement();
    }
}
