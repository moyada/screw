package cn.moyada.screw.pool;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 并发资源池
 * @author xueyikang
 * @create 2018-05-29 19:49
 */
public class ConcurrentBeanPool<T> extends BeanPoolFactory<T> {

    /**
     * 头指针
     */
    protected final AtomicReference<Item> first;

    /**
     * 尾指针
     */
    protected final AtomicReference<Item> last;

    ConcurrentBeanPool(Supplier<T> defaultBean) {
        super(defaultBean);
        this.first = new AtomicReference<>(null);
        this.last = new AtomicReference<>(null);
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

        Item item, next;

        // 取出头指针资源，替换下一个资源
        do {
            item = first.get();
            next = item.next;
        }
        while (!first.compareAndSet(item, next));

        // 新资源为空，清空尾指针
        if(null == next) {
            System.out.println("88");
            // 当回收资源同时发生时
            if(!last.compareAndSet(item, null)) {
                System.out.println("null");
                first.compareAndSet(null, item.next);
            }
        }

        return item.value;
    }

    @Override
    protected boolean isEmpty() {
        return first.compareAndSet(null, null);
    }

    @Override
    public void recycle(T bean) {
        Item next = new Item(bean); // 创建新资源
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
    }
}
