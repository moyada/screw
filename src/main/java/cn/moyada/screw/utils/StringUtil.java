package cn.moyada.screw.utils;


/**
 * Created by xueyikang on 2017/2/23.
 */
public class StringUtil {

    public static final boolean isEmpty(String str) {
        if(null == str) {
            return true;
        }
        int length = str.length();
        if(0 == length) {
            return true;
        }
        for (int index = 0; index < length; index++) {
            if(' ' != str.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    public static String concat(final Object... objs) {
        int length = objs.length;
        String[] strings = new String[length];

        String str;
        int count = 0, size, index;

        for (index = 0; index < length; index++) {
            if(objs[index] instanceof Object[]) {
                str = toString(Object[].class.cast(objs[index]));
            }
            else {
                str = objs[index].toString();
            }
            size = str.length();
            count += size;
            strings[index] = str;
        }

        char[] chs = new char[count];

        for (index = 0, count = 0; index < length; index++) {
            str = strings[index];
            size = str.length();
            str.getChars(0, size, chs, count);
            count += size;
        }
        return new String(chs);
    }

    private static final String toString(final Object[] objs) {
        int length = objs.length;

        String[] strings = new String[length];

        int count = 0, size, index;
        String str;
        for (index = 0; index < length; index++) {
            str = objs[index].toString();
            size = str.length();
            strings[index] = str;
            count += size;
        }

        char[] chs = new char[count];
        for (index = 0, count = 0; index < length; index++) {
            str = strings[index];
            size = str.length();
            str.getChars(0, size, chs, count);
            count += size;
        }
        return new String(chs);
    }


    public static void main(String[] args) {
        Object[] objs = new Object[]{"model", "souche", new Object[]{12, "32000"}, "666"};

        System.out.println(concat(objs));
    }
}
