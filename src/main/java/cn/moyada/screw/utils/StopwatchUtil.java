package cn.moyada.screw.utils;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;
import cn.moyada.screw.watch.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-06-25 17:21
 */
public class StopwatchUtil {

    private static BeanPool<Stopwatch> executor = BeanPoolFactory.newConcurrentPool(10, Stopwatch::createUnstarted);

    public static Stopwatch start() {
        Stopwatch stopwatch = executor.allocate();
        stopwatch.reset();
        return stopwatch;
    }

    public static long stop(Stopwatch stopwatch) {
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        executor.recycle(stopwatch);
        return elapsed;
    }
}
