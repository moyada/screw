package cn.moyada.screw.utils;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

/**
 * 系统工具
 * @author xueyikang
 * @create 2018-04-13 10:30
 */
public class RuntimeUtil {

    /**
     * 获取FullGC次数
     * @return
     */
    public static long getGCCount() {
        long gcCount = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcCount += garbageCollectorMXBean.getCollectionCount();
        }
        return gcCount;
    }
}
