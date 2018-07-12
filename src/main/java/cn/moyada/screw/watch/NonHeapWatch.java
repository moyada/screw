package cn.moyada.screw.watch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author xueyikang
 * @create 2018-07-12 02:53
 */
public class NonHeapWatch {

    private MemoryUsage nonHeapMemoryUsage;

    public NonHeapWatch() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        nonHeapMemoryUsage.getMax();
    }

    public double getUsedRate() {
        return 0d;
    }

    public long getMax() {
        return nonHeapMemoryUsage.getMax();
    }
}
