import javassist.ClassPool;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储类的信息、方法数据、方法代码等。
 * @author xueyikang
 * @create 2018-05-19 11:53
 */
public class MetaSpaceTest {

    static String base = "string";

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);
        System.out.println("start");
        primitive();
    }

    public static void cl() {
        ClassPool pool = ClassPool.getDefault();
        try {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                pool.makeClass("cn.moyada.screw.lock.StampedLockDemo" + i).toClass();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void string() {
        List<String> list = new ArrayList<String>();
        for (int i=0;i< Integer.MAX_VALUE;i++){
            String str = base + base;
            base = str;
            list.add(str.intern());
        }
    }

    public static void integer() {
        List<Integer> list = new ArrayList<>();
        for (int i=0;i< Integer.MAX_VALUE;i++){
            list.add(i);
        }
    }

    public static void primitive() {
        int[] ints = new int[Integer.MAX_VALUE / 2];
        int length = (Integer.MAX_VALUE / 2) - 1;
        for (int i=0;i < length; i++){
            ints[i] = i;
        }
    }
}
