package cn.moyada.screw.lock;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xueyikang
 * @create 2018-05-24 11:26
 */
public class ConditionTest {

    private LinkedList<String> buffer;    //容器
    private int maxSize ;           //容器最大
    private Lock lock;
    private Condition fullCondition;
    private Condition notFullCondition;

    ConditionTest(int maxSize){
        this.maxSize = maxSize;
        buffer = new LinkedList<String>();
        lock = new ReentrantLock();
        fullCondition = lock.newCondition();
        notFullCondition = lock.newCondition();
    }

    public void set(String string) throws InterruptedException {
        lock.lock();    //获取锁
        try {
            while (maxSize == buffer.size()){
                System.out.println("------full------");
                notFullCondition.await();       //满了，添加的线程进入等待状态
                System.out.println("------not full------");
            }
            buffer.add(string);
            fullCondition.signal();
        } finally {
            lock.unlock();      //记得释放锁
        }
    }

    public String get() throws InterruptedException {
        String string;
        lock.lock();
        try {
            while (buffer.size() == 0){
                System.out.println("------await------");
                fullCondition.await();
                System.out.println("------wake up------");
            }
            string = buffer.poll();
            notFullCondition.signal();
        } finally {
            lock.unlock();
        }
        return string;
    }

    public static void main(String[] args) throws InterruptedException {
        ConditionTest conditionTest = new ConditionTest(20);

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println(conditionTest.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    conditionTest.set("test" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.currentThread().join();
    }
}