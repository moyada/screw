package cn.moyada.screw.pool;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 对象资源池
 * @author xueyikang
 * @create 2018-05-29 19:21
 */
public abstract class BeanPoolFactory {

    protected final static int DEFAULT_SIZE = 1024;

    public static <T> BeanPool<T> newPool(Supplier<T> defaultBeanFactory) {
        return newPool(DEFAULT_SIZE, defaultBeanFactory);
    }

    public static <T> BeanPool<T> newPool(int size, Supplier<T> defaultBeanFactory) {
        return newPool(size, defaultBeanFactory, false);
    }

    public static <T> BeanPool<T> newConcurrentPool(Supplier<T> defaultBeanFactory) {
        return newConcurrentPool(DEFAULT_SIZE, defaultBeanFactory);
    }

    public static <T> BeanPool<T> newConcurrentPool(int size, Supplier<T> defaultBeanFactory) {
        return newPool(size, defaultBeanFactory, true);
    }

    private static <T> BeanPool<T> newPool(int size, Supplier<T> defaultBeanFactory, boolean synchronize) {
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
        BeanPool<String> executor = BeanPoolFactory.newConcurrentPool(20, () -> finalDefaultBean);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        defaultBean = null;

        executor.recycle("haha");

        Future<?> submit = pool.submit(() -> System.out.println(executor.allocate()));
        try {
            submit.get(100L, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            System.out.println("error");
            submit.cancel(true);
        } catch (TimeoutException e) {
            System.out.println("timeout");
            submit.cancel(true);
        }
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
