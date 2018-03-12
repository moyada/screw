package cn.moyada.screw.utils;


/**
 * Created by xueyikang on 2017/2/23.
 */
public class StringUtil {

    private static final String EMPTY = "_";

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

    public static final boolean isEmpty(Object[] objs) {
        return null == objs || objs.length == 0;
    }

    public static final String concat(final Object... objs) {
        if(isEmpty(objs)) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();

        int length = objs.length;
        for (int index = 0; index < length; index++) {
            if(objs[index] instanceof Object[]) {
                sb.append(arr2str(Object[].class.cast(objs[index])));
            }
            else {
                sb.append(objs[index].toString());
            }
        }
        return sb.toString().intern();
    }

    private static final String arr2str(final Object[] objs) {
        if(null == objs) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();

        int length = objs.length;
        for (int index = 0; index < length; index++) {
            sb.append(toString(objs[index]));
        }
        return sb.toString().intern();
    }


    private static final String toString(final Object obj) {
        if(null == obj) {
            return EMPTY;
        }
        String str;
        if(obj.getClass().isArray()) {
            str = arr2str(Object[].class.cast(obj));
        }
        else {
            str = obj.toString();
        }
        return str;
    }


    public static void main(String[] args) {
        Object[] objs = new Object[]{"model", "souche", new Object[]{new Object[]{12, "32000"}, "32000"}, "666"};

        System.out.println(concat(objs));

        System.out.println(toString(12.43d));
    }
}
