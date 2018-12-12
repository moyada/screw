package cn.moyada.screw.jvm;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface MemoryUtil {

    default MemoryUsage getMemoryInfo(MemoryMXBean memBean, boolean heap) {
        MemoryUsage memoryUsage;
        if (heap) {
            memoryUsage = memBean.getHeapMemoryUsage();
        } else {
            memoryUsage = memBean.getNonHeapMemoryUsage();
        }
        return memoryUsage;
    }

    default long geUsed(MemoryUsage memoryUsage) {
        return memoryUsage.getMax();
    }

    default long getSize(MemoryUsage memoryUsage) {
        return memoryUsage.getMax();
    }

    default long getCommited(MemoryUsage memoryUsage) {
        return memoryUsage.getCommitted();
    }
}
