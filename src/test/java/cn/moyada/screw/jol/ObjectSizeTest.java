package cn.moyada.screw.jol;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class ObjectSizeTest {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("dfdsfdsfdsfdsffdsfdsfd").append(23354).append('d').append('c').append("6666");

        Map<Integer, String> map = new HashMap<>();
        map.put(1, "666");
        map.put(3, "233");
        map.put(4, "---");
        map.put(5, " ");
        map.put(0, "nodedeede");
        map.put(2, "sdfsf");
        map.put(9, "121121");
        map.put(7, "erfds");
        map.put(8, "aaaa");
        map.put(10, "hahaha");
        map.put(11, "qweqw");
        map.put(12, "ddddd");
        map.put(13, "123");

        List<Map> list = new ArrayList<>();
        list.add(map);

        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
        System.out.println(ClassLayout.parseClass(StringBuilder.class).toPrintable(sb));
        System.out.println(ClassLayout.parseInstance(map).toPrintable());
        System.out.println(ClassLayout.parseInstance(list).toPrintable());
        System.out.println(ClassLayout.parseInstance(list).instanceSize());
    }
}
