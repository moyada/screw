package cn.moyada.screw.deque;

/**
 * @author xueyikang
 * @create 2018-04-09 22:34
 */
public class LinkedDeque<E> extends AbstractDeque<E> {

    // 头指针
    protected Node<E> top;

    // 尾指针
    protected Node<E> end;

    public LinkedDeque() {
        this.top = null;
        this.end = null;
    }

    @Override
    public void reverse() {
        direction = !direction;
    }

    @Override
    public void offerFirst(E value) {
        if(direction) {
            offerFirstNode(value);
            return;
        }
        offerLastNode(value);
    }

    @Override
    public void offerLast(E value) {
        if(direction) {
            offerLastNode(value);
            return;
        }
        offerFirstNode(value);
    }

    private void offerFirstNode(E value) {
        if(null == top) {
            offer(value);
            return;
        }

        Node<E> tmp = top;
        top = new Node<E>(value);
        tmp.prev = top;
        top.next = tmp;
    }

    private void offerLastNode(E value) {
        if(null == end) {
            offer(value);
            return;
        }

        Node<E> tmp = end;
        end = new Node<E>(value);
        tmp.next = end;
        end.prev = tmp;
    }

    private void offer(E value) {
        top = new Node<E>(value);
        end = top;
    }

    @Override
    public E pollFirst() {
        if(direction) {
            return pollFirstNode();
        }
        return pollLastNode();
    }

    @Override
    public E pollLast() {
        if(direction) {
            return pollLastNode();
        }
        return pollFirstNode();
    }

    private E pollFirstNode() {
        Node<E> eNode = top;
        top = eNode.next;
        if(top == null) {
            end = null;
            return eNode.value;
        }
        top.prev = null;
        return eNode.value;
    }

    private E pollLastNode() {
        Node<E> eNode = end;
        end = eNode.prev;
        if(end == null) {
            top = null;
            return eNode.value;
        }
        end.next = null;
        return eNode.value;
    }

    private class Node<T> extends Padding {

        T value;

        Node<T> prev;

        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    private abstract class Padding {
        private volatile long l1, l2, l3, l4, l5;

        private long getPadding() {
            return l1 + l2 + l3 + l4 + l5;
        }
    }


    public static void main(String[] args) {
        LinkedDeque<String> deque = new LinkedDeque<>();

        deque.offerFirst("666");
        deque.offerFirst("233");
        deque.offerLast("123");

        deque.reverse();

        deque.offerFirst("haha");
        deque.offerFirst("laotie");


        deque.reverse();
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollLast());
        System.out.println(deque.pollFirst());
        deque.reverse();
        System.out.println(deque.pollFirst());
        System.out.println(deque.pollFirst());
    }
}
