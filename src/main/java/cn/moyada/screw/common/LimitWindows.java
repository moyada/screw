package cn.moyada.screw.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class LimitWindows implements Limit {

    private final int second;

    private final Node head;

    private final AtomicReference<Node> end;

    public LimitWindows(int second, int accept) {
        if (second < 1) {
            throw new IllegalStateException("second must bigger than 0.");
        }
        if (accept < 1) {
            throw new IllegalStateException("accept must bigger than 0.");
        }
        this.second = second * 1000;
        this.head = new Node();

        Node prev = head;
        Node next = null;
        for (int i = 0; i < accept; i++) {
            next = new Node(-1);
            prev.next.set(next);
            prev = next;
        }

        end = new AtomicReference<>();
        end.set( next);
    }

    @Override
    public boolean acquire(long currentTime) {
        Node first, next;
        do {
            first = head.next.get();

            if (currentTime - first.time <= second) {
                return false;
            }
            next = first.next.get();
        } while (!head.next.compareAndSet(first, next));

        addEnd(new Node(currentTime));

        return true;
    }

    private void addEnd(Node node) {

        Node last = end.get();

        while (!last.next.compareAndSet(null, node)) {
            Thread.yield();
            last = end.get();
        }

        do {
            end.compareAndSet(last, node);
        } while (!node.next.compareAndSet(null, null));
    }

    private class Node {

        long time;

        AtomicReference<Node> next;

        Node() {
            next = new AtomicReference<>();
        }

        Node(long time) {
            this();
            this.time = time;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LimitWindows windows = new LimitWindows(5, 128);

        for (int i = 0; i < 150; i++) {
            System.out.println(windows.acquire(System.currentTimeMillis()));
            TimeUnit.MILLISECONDS.sleep(50L);
            System.out.println(windows.acquire(System.currentTimeMillis()));
        }
    }
}
