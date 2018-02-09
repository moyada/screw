package cn.moyada.screw.common;

/**
 * @author xueyikang
 * @create 2018-02-08 17:17
 */
public final class SnowFlake {

    private static int order = 0;

    private static final long START_TIME;

    private static long NOW_TIME;

    private static final int machineId;

    private static final long TIME_BIT;

    private static final int MACHINE_BIT;

    private static final int ORDER_BIT;

    private static final int MACHINE_DIGIT = 10;

    private static final int ORDER_DIGIT = 12;

    private static final int LOW_DIGIT = MACHINE_DIGIT + ORDER_DIGIT;

    static {
        // 2^64 - 1 => 1*63bit
        long all = Long.MAX_VALUE;
        // 2^31 - 1 - 2^9 = 2^22 - 1 => 1*21bit
        int low = Integer.MAX_VALUE >>> (MACHINE_DIGIT - 1);

        // high 41 bit(1~42) of 64
        TIME_BIT = all ^ low;
        // low 12 bit(53~64) of 64
        ORDER_BIT = low >>> MACHINE_DIGIT;
        // middle 10 bit(43~52) of 64
        MACHINE_BIT = ORDER_BIT << ORDER_DIGIT;

        try {
            machineId = (genMachineId() << ORDER_DIGIT) & MACHINE_BIT;
        } catch (java.net.UnknownHostException | java.net.SocketException e) {
            throw new RuntimeException(e);
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(2000, java.util.Calendar.JANUARY, 1, 0, 0, 0);

        START_TIME = millisToSecond(calendar.getTimeInMillis());
        NOW_TIME = millisToSecond(System.currentTimeMillis());
    }

    private SnowFlake() {
    }

    private static int genMachineId() throws java.net.UnknownHostException, java.net.SocketException {
        byte[] mac = java.net.NetworkInterface.getByInetAddress(java.net.InetAddress.getLocalHost()).getHardwareAddress();

        int machineId = 0;
        int bit = 0;

        for (int b : mac) {
            if(b < 0) {
                machineId += ((-b + 1) << bit++);
            }
            else {
                machineId += (b << bit++);
            }
        }
        return machineId;
    }

    public static long generateId() {
        long time = getNow();
        return genTime(time) | machineId | genOrder(time);
    }

    private static final long getNow() {
        return millisToSecond(System.currentTimeMillis());
    }

    /**
     * generate time start with 2000 year
     * @param time
     * @return
     */
    private static final long genTime(long time) {
        return ((time - START_TIME) << LOW_DIGIT) & TIME_BIT;
    }

    private static final long millisToSecond(long millis) {
        return millis / 1000L;
    }

    private static final int genOrder(long time) {
        synchronized(SnowFlake.class) {
            if (time == NOW_TIME) {
                return ++order & ORDER_BIT;
            }
            NOW_TIME = time;
            return (order = 1);
        }
    }

    public static void main(String[] args) {
        System.out.println(Long.MAX_VALUE);
    }
}