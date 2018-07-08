package cn.moyada.screw.deque;

/**
 * @author xueyikang
 * @create 2018-04-09 22:32
 */
public abstract class AbstractDeque<E> {

    // 方向
    protected boolean direction;

    public AbstractDeque() {
        this.direction = true;
    }

    /**
     * 反向
     */
    public abstract void reverse();

    /**
     * 头插入
     * @param value
     */
    public abstract void offerFirst(E value);

    /**
     * 尾插入
     * @param value
     */
    public abstract void offerLast(E value);

    /**
     * 头取出
     * @return
     */
    public abstract E pollFirst();

    /**
     * 尾取出
     * @return
     */
    public abstract E pollLast();
}
