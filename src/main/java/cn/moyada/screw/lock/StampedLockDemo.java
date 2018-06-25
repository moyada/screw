package cn.moyada.screw.lock;

import java.util.concurrent.locks.StampedLock;

/**
 * @author xueyikang
 * @create 2018-05-17 04:25
 */
public class StampedLockDemo {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    void move(double deltaX, double deltaY) { // an exclusively locked method
        // increment stamp
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    double distanceFromOrigin() { // A read-only method
        long stamp = sl.tryOptimisticRead();
        double currentX = x, currentY = y;
        // if has write task or read state is invalid
        if (!sl.validate(stamp)) {
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    public static void main(String[] args) {
        StampedLockDemo lock = new StampedLockDemo();

        lock.move(12d, 4d);
        System.out.println(lock.distanceFromOrigin());
        Thread.currentThread().interrupt();
        System.out.println(lock.distanceFromOrigin());
        System.out.println(lock.distanceFromOrigin());
        System.out.println(lock.distanceFromOrigin());
    }
}
