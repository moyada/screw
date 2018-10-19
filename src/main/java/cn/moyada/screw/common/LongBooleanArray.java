package cn.moyada.screw.common;

import cn.moyada.screw.utils.CommonUtil;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.lang.ref.Cleaner;

public class LongBooleanArray implements Serializable, AutoCloseable {

    private static final long serialVersionUID = 5768665824796045492L;

    private final byte TRUE = 1;
    private final byte FALSE = 0;

    private final Unsafe UNSAFE;

    private final long address;

    private final long size;

    private final Cleaner.Cleanable cleaner;

    public LongBooleanArray(long size) {
        this.UNSAFE = CommonUtil.getUnsafe();
        this.address = UNSAFE.allocateMemory(size);
        this.size = size;

        this.cleaner = Cleaner.create().register(this, () -> this.UNSAFE.freeMemory(address));
    }

    public boolean get(long index) {
        checkIndex(index);
        return UNSAFE.getByte(address + index) == TRUE;
    }

    public void put(long index, boolean value) {
        checkIndex(index);
        UNSAFE.putByte(address + index, value ? TRUE : FALSE);
    }

    private void checkIndex(long index) {
        if(index > address + size) {
            throw new IndexOutOfBoundsException("Max index is " + size + ", but current index is " + index);
        }
    }

    @Override
    public void close() {
        this.cleaner.clean();
    }

    public static void main(String[] args) {
        LongBooleanArray array = new LongBooleanArray(123L);
        array.put(3L, true);
        System.out.println(array.get(4L));
        System.out.println(array.get(3L));
    }
}
