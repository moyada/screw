package cn.moyada.screw.jvm;

import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import java.lang.reflect.ParameterizedType;

/**
 * @author xueyikang
 * @since 1.0
 **/
public abstract class AbstractNotificationBroadcaster<T> extends NotificationBroadcasterSupport {

    private final Class<T> handType;

    public AbstractNotificationBroadcaster() {
        this.handType = getType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) {
        if (isType(handback)) {
            notificationListener(listener, filter, (T) handback);
        }
    }

    protected abstract void notificationListener(NotificationListener listener, NotificationFilter filter, T handback);

    @SuppressWarnings("unchecked")
    private Class<T> getType() {
        ParameterizedType type = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }

    private boolean isType(Object obj) {
        return handType.isInstance(obj);
    }
}
