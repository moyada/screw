package cn.moyada.screw.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-06-18 12:15
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class DateBenchMark {

    @Benchmark
    public Calendar runCalendar() {
        return Calendar.getInstance();
    }

    @Benchmark
    public Timestamp runJoda() {
        return Timestamp.from(Instant.now());
    }

    @Benchmark
    public long runSystem() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DateBenchMark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}