package cn.moyada.screw.jvm;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface ThreadUtil {

    default int countThread(ThreadMXBean threadMXBean) {
        return threadMXBean.getThreadCount();
    }

    default String[] getThread(ThreadMXBean threadMXBean, Thread.State state) {
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        List<String> threads = new ArrayList<>(threadInfos.length);
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo.getThreadState() == state) {
                threads.add(threadInfo.getThreadName());
            }
        }
        return threads.toArray(new String[0]);
    }
}
