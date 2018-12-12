package cn.moyada.screw.jmh;

import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class SynchronizerTest {

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                new Consumer().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                new Consumer().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.SECONDS.sleep(1L);

        new Thread(() -> {
            new Product().run();
        }).start();

        TimeUnit.SECONDS.sleep(2L);

        new Thread(() -> {
            new Product().run();
        }).start();

        Thread t4 = new Thread(() -> {
            try {
                new Consumer().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t4.start();
        t4.join();

        new Thread(() -> {
            try {
                new Consumer().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        TimeUnit.SECONDS.sleep(10L);
    }


    private static final Object lock = new Object();

    static class Consumer {

        public void run() throws InterruptedException {
            synchronized (lock) {
                for (int i = 0; i < 5; i++) {
                    System.out.println("prewait " + Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(200L);
                }
                System.out.println("lock");
                lock.wait();
                for (int i = 0; i < 5; i++) {
                    System.out.println("afterwait " + Thread.currentThread().getName());
                    TimeUnit.MILLISECONDS.sleep(600L);
                }
            }

            System.out.println("ok");
        }

    }

    static class Product {

        public void run() {
            synchronized (lock) {
                lock.notifyAll();
                System.out.println("unlock");

                try {
                    TimeUnit.MILLISECONDS.sleep(200L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("--");
        }
    }
}
