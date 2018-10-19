package common;

import cn.moyada.screw.model.OrderDO;
import cn.moyada.screw.model.OrderDetailDO;
import cn.moyada.screw.utils.CloneUtil;

/**
 * @author xueyikang
 * @create 2018-03-01 21:30
 */
public class MapTest {
    public static void main(String[] args) {
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderCode(12344231L);
        orderDO.setPhone("135937673");
        OrderDetailDO orderDO1 = CloneUtil.copyToObject(orderDO, OrderDetailDO.class);
        System.out.println(orderDO1);

//        EnumMap<Type, String> enumMap = new EnumMap<>(Type.class);
//        enumMap.put(Type.A, "haha");
//        enumMap.put(Type.B, "666");
//        System.out.println(enumMap.get(Type.A));
//
//        enumMap.forEach((k, v) -> System.out.println(k.ordinal() + v));
//
//        EnumMap<Faker, String> enumMap2 = new EnumMap<>(Faker.class);
//        enumMap2.put(Faker.FSD, "haha");
//        System.out.println(enumMap2.get(Faker.GF));

//        BiMap<String, Integer> biMap = new BiMap<>(6);
//        biMap.put("haha", 123);
//        biMap.put("wawa", 222);
//        biMap.put("zzz", 3);
//        biMap.put("h", 1);
//        biMap.put("xx", 8);
//        biMap.put("wawa", 44);
//        biMap.put("666", 24);
//        biMap.put("233", 233);
//
//        biMap.forEach((k, v) -> System.out.println(k + v));
//
//        System.out.println("----");
//        System.out.println(biMap.get("666"));
//        System.out.println(biMap.get("233"));
//        System.out.println(biMap.get("233="));
//        System.out.println(biMap.getKey(8));
//        System.out.println(biMap.getKey(88));
    }
}
