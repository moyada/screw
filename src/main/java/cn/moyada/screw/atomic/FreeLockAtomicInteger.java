package cn.moyada.screw.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class FreeLockAtomicInteger extends AtomicInteger {

    private static final long serialVersionUID = 3066810431121278028L;

    public FreeLockAtomicInteger(int initialValue) {
        super(initialValue);
    }

    public FreeLockAtomicInteger() {
        this(0);
    }

    public int freeIncrementAndGet() {
        int update = get() + 1;
        for (int current = get(); !compareAndSet(current, update); current = get(), update = current + 1)
        {
            Thread.yield();
        }

        return update;
    }

    public int freeDecrementAndGet() {
        int update = get() - 1;
        for (int current = get(); !compareAndSet(current, update); current = get(), update = current - 1)
        {
            Thread.yield();
        }

        return update;
    }
}
