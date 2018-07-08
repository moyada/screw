package cn.moyada.screw.deque;

/**
 * @author xueyikang
 * @create 2018-04-09 22:32
 */
public class ArrayDeque<E> extends AbstractDeque<E> {

    private static final int DEFAULT_SIZE = 16;

    // 头指针
    protected int top;

    // 尾指针
    protected int end;

    protected int size;

    private Node<E>[] data;

    public ArrayDeque() {
        this(DEFAULT_SIZE);
    }

    @SuppressWarnings("unchecked")
    public ArrayDeque(int size) {
        if(size < 1) {
            throw new IllegalArgumentException("size must be great than 1.");
        }
        this.top = size - 1;
        this.end = size - 1;
        size = size * 2;
        this.size = size;
        this.data = new Node[size];
        this.direction = true;
    }

    /**
     * 反向
     */
    public void reverse() {
        direction = !direction;
    }

    /**
     * 头插入
     * @param value
     */
    @Override
    public void offerFirst(E value) {
        if(direction) {
            offerFirstNode(value);
            return;
        }
        offerLastNode(value);
    }

    /**
     * 尾插入
     * @param value
     */
    @Override
    public void offerLast(E value) {
        if(direction) {
            offerLastNode(value);
            return;
        }
        offerFirstNode(value);
    }

    private void offerFirstNode(E value) {
        if(null == data[top]) {
            offsetNode(value);
            return;
        }
        if(top == 1)
            extending();

        int index = top;
        index--;
        data[index] = new Node<>(value);
        top = index;
    }

    private void offerLastNode(E value) {
        if(null == data[end]) {
            offsetNode(value);
            return;
        }
        if(end == (size - 1))
            extending();

        int index = end;
        index++;
        data[index] = new Node<>(value);
        end = index;
    }

    @SuppressWarnings("unchecked")
    private void extending() {
        int size = this.size * 2;
        int start = this.size / 2;
        Node[] nodes = new Node[size];
        System.arraycopy(this.data, 0, nodes, start, this.size);
        this.size = size;
        this.data = nodes;
        this.top = this.top + start;
        this.end = this.end + start;
    }

    private void offsetNode(E value) {
        data[top] = new Node<>(value);
    }

    /**
     * 头取出
     * @return
     */
    public E pollFirst() {
        if(direction) {
            return pollFirstNode();
        }
        return pollLastNode();
    }

    /**
     * 尾取出
     * @return
     */
    public E pollLast() {
        if(direction) {
            return pollLastNode();
        }
        return pollFirstNode();
    }

    private E pollFirstNode() {
        Node<E> node = data[top];
        if(top == end) {
            if(null == node) {
                return null;
            }
            data[top] = null;
            return node.value;
        }
        top++;
        return node.value;
    }


    private E pollLastNode() {
        Node<E> node = data[end];
        if(top == end) {
            if(null == node) {
                return null;
            }
            data[end] = null;
            return node.value;
        }
        end--;
        return node.value;
    }

    private class Node<T> extends Padding{

        T value;

        Node(T value) {
            this.value = value;
        }
    }

    private abstract class Padding {
        private volatile long l1, l2, l3, l4, l5, l6, l7;

        private long getPadding() {
            return l1 + l2 + l3 + l4 + l5 + l6 + l7;
        }
    }

    public static void main(String[] args) {
        ArrayDeque<String> deque = new ArrayDeque<>(3);

        deque.offerFirst("666");
        deque.offerFirst("233");
        deque.offerLast("123");

        deque.reverse();

        deque.offerFirst("haha");
        deque.offerFirst("laotie");
        deque.reverse();
        deque.offerFirst("hehe");


        deque.reverse();
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollLast());
        deque.reverse();
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollLast());
        System.out.println(deque.pollFirst());
    }
}
