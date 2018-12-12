package cn.moyada.screw.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class ThreadLocalPoolExecutor extends ThreadPoolExecutor {

    public ThreadLocalPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public ThreadLocalPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ThreadLocalPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadLocalPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new ThreadRunnable(command));
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r instanceof ThreadRunnable) {
            AsyncThreadLocal.injectThreadLocal((ThreadRunnable) r);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        AsyncThreadLocal.remoteThreadLocal();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "super");
        hashMap.put("invoke", "123");
        AsyncThreadLocal.threadLocal.set(hashMap);

        Thread.sleep(100L);

        AtomicInteger count = new AtomicInteger();

        new Thread(() -> {
            Map<String, Object> map = AsyncThreadLocal.threadLocal.get();
            map = new HashMap<>(map);
            map.put("name", Thread.currentThread().getName());
            map.put("invoeAsync", count.getAndIncrement());
            System.out.println(map);
        }).start();

        ExecutorService executor = new ThreadLocalPoolExecutor(1, 1, 123L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
        executor.execute(() -> {
            Map<String, Object> map = AsyncThreadLocal.threadLocal.get();
            map.put("name", Thread.currentThread().getName());
            map.put("invoeAsync", count.getAndIncrement());
            System.out.println(map);
        });

        Future<Object> submit = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Map<String, Object> map = AsyncThreadLocal.threadLocal.get();
                map.put("name", Thread.currentThread().getName());
                map.put(String.valueOf(count.getAndIncrement()), "invoeAsync");
                System.out.println(map);
                return map;
            }
        });

        System.out.println(submit.get());

        Thread.sleep(1000L);
        executor.shutdown();
        System.out.println(hashMap);
    }
}
