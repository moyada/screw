package cn.moyada.screw.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * 对象资源池
 * @author xueyikang
 * @create 2018-05-29 19:21
 */
public abstract class BeanPoolFactory {

    protected final static int DEFAULT_SIZE = 1024;

    public static <T> BeanPool<T> newPool(T defaultBean) {
        return newPool(defaultBean, false);
    }

    public static <T> BeanPool<T> newPool(T defaultBean, boolean synchronize) {
        return newPool(DEFAULT_SIZE, defaultBean, synchronize);
    }

    public static <T> BeanPool<T> newPool(int size, T defaultBean, boolean synchronize) {
        if(null == defaultBean) {
            throw new IllegalArgumentException("defaultBean can not be null.");
        }
        final T bean = defaultBean;
        return newPool(size, () -> bean, synchronize);
    }

    public static <T> BeanPool<T> newPool(Supplier<T> defaultBeanFactory) {
        return newPool(defaultBeanFactory, false);
    }

    public static <T> BeanPool<T> newPool(Supplier<T> defaultBeanFactory, boolean synchronize) {
        return newPool(DEFAULT_SIZE, defaultBeanFactory, synchronize);
    }

    public static <T> BeanPool<T> newPool(int size, Supplier<T> defaultBeanFactory, boolean synchronize) {
        if(size <= 0) {
            throw new IllegalArgumentException("size can not be positive.");
        }
        if(null == defaultBeanFactory) {
            throw new IllegalArgumentException("defaultBeanFactory can not be null.");
        }

        if(synchronize) {
            return new ConcurrentBeanPool<>(size, defaultBeanFactory);
        }
        return new SingleBeanPool<>(size, defaultBeanFactory);
    }

    public static void main(String[] args) throws InterruptedException {
        String defaultBean = "666";
        String finalDefaultBean = defaultBean;
        BeanPool<String> executor = BeanPoolFactory.newPool(20, () -> finalDefaultBean, true);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        defaultBean = null;

        executor.recycle("haha");
        pool.execute(() -> System.out.println(executor.allocate()));
        pool.execute(() -> System.out.println(executor.allocate()));
        pool.execute(() -> executor.recycle("heihei"));


        for (int i = 0; i < 20; i++) {
            pool.execute(() -> System.out.println(executor.allocate()));
        }

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.execute(() -> executor.recycle(Integer.toString(finalI)));
        }

        Thread.sleep(5000L);

        Thread.currentThread().join();
    }
}
