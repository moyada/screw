package cn.moyada.screw.jvm;

import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface NotificationHandler<T> extends NotificationListener {

    @SuppressWarnings("unchecked")
    default void handleNotification(Notification notification, Object handback) {
        handleNotification0(notification, (T) handback);
    }

    void handleNotification0(Notification notification, T handback);
}
