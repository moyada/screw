package cn.moyada.screw.jvm;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface NotificationUtil {

    default <T> void addListener(AbstractNotificationBroadcaster<T> broadcaster,
                                             NotificationHandler<T> listener, T back) {
        broadcaster.addNotificationListener(listener, null, back);
    }
}
