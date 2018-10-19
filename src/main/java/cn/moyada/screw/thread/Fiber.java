package cn.moyada.screw.thread;

import java.util.concurrent.Callable;

public class Fiber extends Thread {

    private FiberTask head;

    @Override
    public void run() {
        while (head.hasNext()) {

        }
        interrupt();
    }

    class FiberTask<V> {

        FiberTask nextTask;

        Runnable runnable;

        Callable<V> callable;

        public FiberTask(Runnable runnable) {
            this.runnable = runnable;
        }

        public FiberTask(Callable<V> callable) {
            this.callable = callable;
        }

        public boolean hasNext() {
            return nextTask != null;
        }
    }
}
