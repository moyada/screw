package cn.moyada.screw.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 对象资源池
 * @author xueyikang
 * @create 2018-05-29 19:21
 */
public abstract class BeanPoolFactory<T> implements BeanPool<T> {

    /**
     * 新对象生成器
     */
    protected Supplier<T> defaultBean;

    BeanPoolFactory(Supplier<T> defaultBean) {
        this.defaultBean = defaultBean;
    }

    public static <T> BeanPool<T> newPool(Supplier<T> defaultBean) {
        return newPool(defaultBean, false);
    }

    public static <T> BeanPool<T> newPool(Supplier<T> defaultBean, boolean synchronize) {
        if(null == defaultBean) {
            throw new IllegalArgumentException("defaultBean can not be null.");
        }

        if(synchronize) {
            return new ConcurrentBeanPool<>(defaultBean);
        }
        return new SingleBeanPool<>(defaultBean);
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

    public static void main(String[] args) throws InterruptedException {
        BeanPool<String> executor = BeanPoolFactory.newPool(() -> "666", true);
        ExecutorService pool = Executors.newFixedThreadPool(4);

        executor.recycle("haha");
        pool.execute(() -> System.out.println(executor.allocate()));
        pool.execute(() -> executor.recycle("heihei"));

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.execute(() -> executor.recycle(Integer.toString(finalI)));
        }

        for (int i = 0; i < 100; i++) {
            pool.execute(() -> System.out.println(executor.allocate()));
        }

        TimeUnit.SECONDS.sleep(3);
        Thread.currentThread().join();
    }
}
