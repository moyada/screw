package cn.moyada.screw.common;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface Limit {

    boolean acquire(long currentTime);
}
