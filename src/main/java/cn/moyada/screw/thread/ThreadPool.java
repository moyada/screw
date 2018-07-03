package cn.moyada.screw.thread;

import java.util.concurrent.*;

public class ThreadPool {

    private final ExecutorService pool;

    public ThreadPool(int threadCore) {
        pool = Executors.newWorkStealingPool(threadCore);
    }

    public void addTask(Runnable command) {
        pool.execute(command);
    }

    public void addTaskTimeout(Runnable command, long milli) {
        Future<?> done = pool.submit(command);

        for (;;) {
            try {
                done.get(milli, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continue;
            } catch (ExecutionException | TimeoutException e) {
                done.cancel(true);
            }
            break;
        }
    }
}
