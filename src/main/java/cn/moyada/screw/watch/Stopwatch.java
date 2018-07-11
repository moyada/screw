package cn.moyada.screw.watch;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author xueyikang
 * @create 2018-07-12 02:39
 */
public class Stopwatch {

    private long startTime;

    public static Stopwatch create() {
        return new Stopwatch(true);
    }

    public static Stopwatch createUnstarted() {
        return new Stopwatch(false);
    }

    private Stopwatch(boolean start) {
        if(start) {
            reset();
        } else {
            startTime = 0;
        }
    }

    private long now() {
        return System.nanoTime();
    }

    private long elapsedNanos() {
        return now() - startTime;
    }

    public long elapsed(TimeUnit timeUnit) {
        return timeUnit.convert(elapsedNanos(), NANOSECONDS);
    }

    public void reset() {
        this.startTime = now();
    }
}
