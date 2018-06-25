package cn.moyada.screw.utils;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-06-25 17:21
 */
public class StopwatchUtil {

    private static BeanPool<Stopwatch> executor = BeanPoolFactory.newPool(20, Stopwatch::createStarted, true);

    public static Stopwatch start() {
        Stopwatch stopwatch = executor.allocate();
        if(stopwatch.isRunning()) {
            stopwatch.reset();
        }
        stopwatch.start();
        return stopwatch;
    }

    public static long stop(Stopwatch stopwatch) {
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.reset();
        executor.recycle(stopwatch);
        return elapsed;
    }
}
