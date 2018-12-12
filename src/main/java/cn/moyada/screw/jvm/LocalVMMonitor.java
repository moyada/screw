package cn.moyada.screw.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class LocalVMMonitor extends AbstractVMMonitor {

    public LocalVMMonitor() {
        OperatingSystemMXBean opMXbean = ManagementFactory.getOperatingSystemMXBean();

    }
}
