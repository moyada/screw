package cn.moyada.screw.jvm;

import cn.moyada.screw.enums.CapacityUnit;

import java.lang.management.*;

/**
 * @author xueyikang
 * @since 1.0
 **/
public abstract class AbstractVMMonitor implements VMMonitor {


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
