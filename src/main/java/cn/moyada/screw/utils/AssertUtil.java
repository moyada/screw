package cn.moyada.screw.utils;

/**
 * @author xueyikang
 * @create 2018-07-12 15:40
 */
public class AssertUtil {

    public static void checkHost(String host) {
        String[] split = host.split("\\.");
        if(split.length != 4) {
            throw new IllegalArgumentException("host error");
        }

        for (String s : split) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("host error");
            }
        }
    }

    public static void checkPort(int port) {
        if(port < 1000) {
            throw new IllegalArgumentException("port must be bigger than 1000");
        }
    }
}
