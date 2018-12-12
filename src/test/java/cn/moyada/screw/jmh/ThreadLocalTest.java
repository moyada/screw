package cn.moyada.screw.jmh;

import cn.moyada.screw.utils.JVMUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class ThreadLocalTest {

    private static ThreadLocal<Node> threadLocal = new ThreadLocal<>();

    private static List<Thread> threads = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {

        System.gc();

        threads.add(new MyThread("2wa"));
        threads.add(new MyThread("6zi"));
        threads.add(new MyThread("wangda"));
        threads.add(new MyThread("wefdgda"));
        threads.add(new MyThread("wasdfda"));
        threads.add(new MyThread("---12"));
        threads.add(new MyThread("梵蒂冈"));
        threads.add(new MyThread("2ee"));
        threads.add(new MyThread("bb55"));
        threads.add(new MyThread("12aaaa"));
        threads.add(new MyThread("zansan"));
        threads.add(new MyThread("77"));
        threads.add(new MyThread("435"));

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(300L);

        System.out.println(JVMUtil.getHeap());

        Thread.sleep(500L);

        System.gc();

        LockSupport.unpark(threads.get(0));

        System.out.println(JVMUtil.getHeap());

        Thread.sleep(500L);
    }

    static class MyThread extends Thread {

        public MyThread(String name) {
            this.setName(name);
        }

        @Override
        public void run() {
            threadLocal.set(new Node(this.getName(), 500));

            LockSupport.park(this);

            for (;;) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(threadLocal.get());
            }
        }
    }

    static class Node {

        private String key;

        private Map<String, Node> map = new HashMap<>();

        public Node(String value, int size) {
            for (int i = 0; i < size; i++) {
                this.map.put(value + i, new Node(value + i));
            }
        }

        public Node(String value) {
            this.key = value;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("destroy");
            super.finalize();
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key='" + key + '\'' +
                    ", map=" + map +
                    '}';
        }
    }
}
