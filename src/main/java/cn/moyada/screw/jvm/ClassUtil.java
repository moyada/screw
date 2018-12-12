package cn.moyada.screw.jvm;

import java.lang.management.ClassLoadingMXBean;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface ClassUtil {

    default int countClassLoader(ClassLoadingMXBean clMXBean) {
        return clMXBean.getLoadedClassCount();
    }
}
