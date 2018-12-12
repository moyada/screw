package cn.moyada.screw.thread;

import java.util.Map;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class AsyncThreadLocal {

    public final static InheritableThreadLocal<Map<String, Object>> threadLocal = new InheritableThreadLocal<>();


    public static void injectThreadLocal(ThreadRunnable tr) {
        Map<String, Object> threadLocal = tr.getThreadLocal();
        if (null == threadLocal) {
            return;
        }
        AsyncThreadLocal.threadLocal.set(threadLocal);
    }

    public static void remoteThreadLocal() {
        threadLocal.remove();
    }
}
