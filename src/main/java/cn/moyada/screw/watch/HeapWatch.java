package cn.moyada.screw.watch;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author xueyikang
 * @create 2018-07-12 02:53
 */
public class HeapWatch {

    public HeapWatch() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        nonHeapMemoryUsage.getMax();
    }
}
