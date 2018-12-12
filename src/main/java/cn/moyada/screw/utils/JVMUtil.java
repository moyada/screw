package cn.moyada.screw.utils;

import cn.moyada.screw.enums.CapacityUnit;

import java.lang.management.*;

/**
 * @author xueyikang
 * @create 2018-07-12 19:42
 */
public final class JVMUtil {

    public static void main(String[] args) {
        System.out.println(getHeap());
    }

    public static String getVM() {
//        CollectedHeap heap = VM.getVM().getUniverse().heap();
//        if (heap instanceof GenCollectedHeap) {
//            GenCollectedHeap gen0 = (GenCollectedHeap) heap;
//
//            Generation youngGen = gen0.getGen(0);
//            if (youngGen instanceof ParNewGeneration) {
//                ParNewGeneration young0 = (ParNewGeneration) youngGen;
//                young0.eden();
//                young0.from();
//                young0.to();
//
//            } else if (youngGen instanceof DefNewGeneration) {
//                DefNewGeneration young0 = (DefNewGeneration) youngGen;
//                young0.eden();
//                young0.from();
//                young0.to();
//
//            } else {
//
//            }
//            Generation oldGen = gen0.getGen(1);
//            if (oldGen instanceof ConcurrentMarkSweepGeneration) {
//                ConcurrentMarkSweepGeneration old0 = (ConcurrentMarkSweepGeneration) oldGen;
//
//
//            } else if (oldGen instanceof TenuredGeneration) {
//                TenuredGeneration old0 = (TenuredGeneration) oldGen;
//
//            } else {
//
//            }
//
//        } else if(heap instanceof ParallelScavengeHeap) {
//            ParallelScavengeHeap gen0 = (ParallelScavengeHeap) heap;
//
//        } else if(heap instanceof G1CollectedHeap) {
//            G1CollectedHeap gen0 = (G1CollectedHeap) heap;
//            G1MonitoringSupport g1mm = gen0.g1mm();
//
//        } else if(heap instanceof ZCollectedHeap) {
//            ZCollectedHeap gen0 = (ZCollectedHeap) heap;
//
//        } else if(heap instanceof EpsilonHeap) {
//            EpsilonHeap gen0 = (EpsilonHeap) heap;
//        }

        return null;
    }

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
