package cn.moyada.screw.tree;

/**
 * @author xueyikang
 * @create 2018-06-13 23:58
 */
public class RedBlackTree {

    private Node root;

    private int level;

    private int leaf;

    private int max;

    public void put(int value) {
        if(null == root) {
            init(new Node(value, true));
            return;
        }

        insertLeaf(value);
    }

    private void init(Node root) {
        this.root = root;
        this.level = 1;
        this.leaf = 0;
        this.max = 2;
    }

    private void insertLeaf(int value) {
        int count = 0;
        boolean isLess = true;

        Node bin = root;

        for (;;) {
            count++;
            if (biggerThan(bin, value)) {
                bin = bin.right;
                if (null == bin) {
                    isLess = false;
                    break;
                }
            } else {
                bin = bin.left;
                if (null == bin.left) {
                    break;
                }
            }
        }

        if(isLess) {
            bin.left = new Node(value, !bin.isBlack, bin);
        }
        else {
            bin.right = new Node(value, !bin.isBlack, bin);
        }

        if(count > level) {
            Node parent = bin.parent.parent;
            if(null == parent) { // is root
                parent = (root = bin);
            }

            if(isLess) {
                parent.right = bin.parent;
            }
        }
        else {

        }

        leaf++;
        if(max == leaf) {
            level++;
            leaf = 0;
            max = max << 1;
        }
    }


    public static boolean biggerThan(Node node, int value) {
        return value > node.value;
    }

    class Node {

        public Node(int value, boolean isBlack, Node parent) {
            this.value = value;
            this.isBlack = isBlack;
            this.parent = parent;
        }

        public Node(int value, boolean isBlack) {
            this(value, isBlack, null);
        }

        int value;

        boolean isBlack;

        Node parent;

        Node left;

        Node right;
    }
}
