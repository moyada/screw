package cn.moyada.screw.utils;

public final class NumberUtil {

    private static final byte[] NUMBER_ASCII = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57};

    public static String fullZero(int value, int length) {
        if(value < 0) {
            throw new IllegalArgumentException("value not support negative number.");
        }
        if(length < 1) {
            throw new IllegalArgumentException("length only support positive number.");
        }

        byte[] bytes = new byte[length--];

        for (int remainder = value % 10; ; remainder = value % 10) {
            bytes[length--] = NUMBER_ASCII[remainder];
            if(length == -1) {
                return new String(bytes);
            }

            if((value /= 10) == 0) {
                break;
            }
        }

        for (; length > -1; length--) {
            bytes[length] = NUMBER_ASCII[0];
        }

        return new String(bytes);
    }

    public static int getMax2PowerMinus1(int source) {
        for (int bit = Integer.MAX_VALUE, next; ; bit = next) {
            if(source == bit) {
                break;
            }
            next = bit >> 1;
            if(source > next) {
                source = bit;
                break;
            }
        }
        return source;
    }
}
