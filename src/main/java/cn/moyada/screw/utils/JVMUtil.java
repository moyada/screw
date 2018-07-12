package cn.moyada.screw.utils;

import cn.moyada.screw.enums.CapacityUnit;

import java.lang.management.*;

/**
 * @author xueyikang
 * @create 2018-07-12 19:42
 */
public class JVMUtil {

    public static String getHeap() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        return getUsedRate(memoryUsage);
    }

    public static String getNonHeap() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        return getUsedRate(memoryUsage);
    }

    public static String getAllThread() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        StringBuilder threads = new StringBuilder(threadInfos.length * 256);
        for (ThreadInfo threadInfo : threadInfos) {
            threads.append(threadInfo.getThreadState().name());
            threads.append(" : ");
            threads.append(threadInfo.getThreadName());
            threads.append("\n");
        }
        return threads.toString();
    }

    private static String getUsedRate(MemoryUsage memoryUsage) {
        long max = memoryUsage.getMax();
        long used = memoryUsage.getUsed();

        if(-1 == max) {
            return getSize(used, CapacityUnit.GB);
        }

        String maxSize = getSize(max, CapacityUnit.GB);
        String usedSize = getSize(used, CapacityUnit.GB);

        return usedSize + " / " + maxSize;
    }

    private static String getSize(long size, CapacityUnit unit) {
        double mb = unit.calculate(size, CapacityUnit.B);
        String num = String.valueOf(mb);
        int index = num.indexOf(".");
        if(index > 0 && (index + 4 < num.length())) {
            num = num.substring(0, index + 4);
        }
        return num + unit.name();
    }
}
