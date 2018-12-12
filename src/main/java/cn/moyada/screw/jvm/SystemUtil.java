package cn.moyada.screw.jvm;


import com.sun.management.OperatingSystemMXBean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface SystemUtil {

    default double getLoadAverage(OperatingSystemMXBean opMXbean) {
        return opMXbean.getSystemLoadAverage();
    }

    default double getUsedRatio(OperatingSystemMXBean opMXbean) {
        return opMXbean.getProcessCpuTime();
    }

    default long getFreeMemorySize(OperatingSystemMXBean opMXbean) {
        return opMXbean.getFreePhysicalMemorySize();
    }

    default long getTotalMemorySize(OperatingSystemMXBean opMXbean) {
        return opMXbean.getTotalPhysicalMemorySize();
    }

    default int countProcessor(OperatingSystemMXBean opMXbean) {
        return opMXbean.getAvailableProcessors();
    }
}
