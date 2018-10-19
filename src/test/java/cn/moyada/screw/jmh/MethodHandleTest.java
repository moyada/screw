package cn.moyada.screw.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MethodHandleTest {

    private static MethodHandle methodHadle;
    private static Method method;

    static {
        try {
            methodHadle = MethodHandles.lookup().findVirtual(MethodHandleTest.class, "calculate", MethodType.methodType(int.class, int.class));
            method = MethodHandleTest.class.getMethod("calculate", int.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void runMethodHandle() throws Throwable {
        methodHadle.invoke(new MethodHandleTest(), 1000);
    }

    @Benchmark
    public void runMethod() throws InvocationTargetException, IllegalAccessException {
        method.invoke(new MethodHandleTest(), 1000);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MethodHandleTest.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    public int calculate(int limit) {
        if(limit < 1) {
            throw new IllegalArgumentException();
        }

        int sum = 0;
        for (int i = 0; i < limit; i++) {
            sum += i;
        }
        return sum;
    }
}
