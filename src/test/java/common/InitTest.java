package common;

/**
 * @author xueyikang
 * @create 2018-03-16 10:50
 */
public class InitTest {

    public static void main(String[] args) {
        System.out.println(Test1.a);
        System.out.println(Test2.a);
        System.out.println(Test3.a);
    }
}

class Test1 {
    static final int a = 100;
    static {
        System.out.println("2");
    }
}

class Test2 {
    static final Integer a = 200;
    static {
        System.out.println("3");
    }
}

class Test3 {
    static final String a = "300";
    static {
        System.out.println("2");
    }
}