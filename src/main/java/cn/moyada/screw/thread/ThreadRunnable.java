package cn.moyada.screw.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class ThreadRunnable implements Runnable {

    private Runnable command;

    private Map<String, Object> threadLocal;

    ThreadRunnable(Runnable command) {
        this.command = command;
        this.threadLocal = AsyncThreadLocal.threadLocal.get();
    }

    @Override
    public void run() {
        command.run();
    }

    Map<String, Object> getThreadLocal() {
        if (null == threadLocal) {
            return null;
        }
        return new HashMap<>(threadLocal);
    }
}
