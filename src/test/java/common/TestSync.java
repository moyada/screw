package common;

import org.openjdk.jmh.annotations.CompilerControl;

/**
 * @author xueyikang
 * @create 2018-05-06 23:36
 */
public class TestSync implements Runnable {

    int b = 100;

    @CompilerControl(CompilerControl.Mode.INLINE)
    synchronized void m1() throws InterruptedException {
        b = 1000;
        System.out.println("run m1 b");
        Thread.sleep(500);
//        this.wait(500);
        System.out.println("b: " + b);
//        this.notifyAll();
    }

    synchronized void m2() throws InterruptedException {
        Thread.sleep(250);
//        this.wait(250);
        System.out.println("run m2 b");
        b = 2000;
//        this.notifyAll();
    }

    @Override
    public void run() {
        try {
            m1();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("--------------------");
            TestSync t = new TestSync();
            Thread thread = new Thread(t);
            thread.start();
            t.m2();
            System.out.println("m. b: " + t.b);
            Thread.sleep(1000);
        }
    }
}
