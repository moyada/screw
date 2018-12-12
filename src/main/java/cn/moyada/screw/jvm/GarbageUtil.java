package cn.moyada.screw.jvm;

import com.sun.management.GarbageCollectorMXBean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface GarbageUtil {

    default long countGC(GarbageCollectorMXBean gcMXBean) {
        return gcMXBean.getCollectionCount();
    }

    default long getGCTime(GarbageCollectorMXBean gcMXBean) {
        return gcMXBean.getCollectionTime();
    }
}
