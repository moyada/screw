package cn.moyada.screw.reflect;

import java.util.concurrent.CyclicBarrier;

public class TestGeneric extends AbstractGeneric<CyclicBarrier> {

    public static void main(String[] args) {
        Class<CyclicBarrier> genericClass = new TestGeneric().getGenericClass();

        System.out.println(genericClass);
    }
}
