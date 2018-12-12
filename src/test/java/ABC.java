import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class ABC {

    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean l1 = new AtomicBoolean(false);
        AtomicBoolean l2 = new AtomicBoolean(false);
        AtomicBoolean l3 = new AtomicBoolean(false);

        Thread t1 = new ThreadA(l2, l1);
        Thread t2 = new ThreadB(l2, l3);
        Thread t3 = new ThreadC(l3, l1);

        t1.start();
        t2.start();
        t3.start();

        Thread.currentThread().join();
    }

    static class ThreadA extends Thread {

        private final AtomicBoolean l2;
        private final AtomicBoolean l1;

        public ThreadA(AtomicBoolean l2, AtomicBoolean l1) {
            this.l2 = l2;
            this.l1 = l1;
        }

        @Override
        public void run() {
            for (;;) {
                System.out.print("A");

                l2.compareAndSet(false, true);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (!l1.compareAndSet(true, false)) {
                }
            }
        }
    }

    static class ThreadB extends Thread {

        private final AtomicBoolean l3;
        private final AtomicBoolean l2;

        public ThreadB(AtomicBoolean l2, AtomicBoolean l3) {
            this.l2 = l2;
            this.l3 = l3;
        }

        @Override
        public void run() {
            for (;;) {
                while (!l2.compareAndSet(true, false)) {
                }

                System.out.print("B");
                l3.compareAndSet(false, true);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ThreadC extends Thread {

        private final AtomicBoolean l3;
        private final AtomicBoolean l1;

        public ThreadC(AtomicBoolean l3, AtomicBoolean l1) {
            this.l3 = l3;
            this.l1 = l1;
        }

        @Override
        public void run() {
            for (;;) {
                while (!l3.compareAndSet(true, false)) {
                }

                System.out.print("C");

                l1.compareAndSet(false, true);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
