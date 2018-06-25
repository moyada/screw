package cn.moyada.screw.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;

/**
 * @author xueyikang
 * @create 2018-02-08 17:17
 */
public final class SnowFlake {
    private static int CURRENT_ORDER;

    private static int LAST_ORDER;

    private static long CURRENT_TIME;

    private static long LAST_TIME;

    private static final long START_TIME;

    private static final int machineId;

    private static final long TIME_BIT;

    private static final int MACHINE_BIT;

    private static final int ORDER_BIT;

    private static final int LOW_DIGIT = 30;

    private static final int ORDER_DIGIT = 20;

    private static final int MACHINE_DIGIT = LOW_DIGIT - ORDER_DIGIT;

    static {
        // 2^64 - 1 => 1*64bit
        long all = Long.MAX_VALUE;

        // 31bit
        int low = (1 << LOW_DIGIT) - 1;

        // high 32 bit(1~33) of 64
        TIME_BIT = all ^ low;

        // low 21 bit(44~64) of 64
        ORDER_BIT = low >>> MACHINE_DIGIT;

        // middle 10 bit(34~43) of 64
        MACHINE_BIT = low ^ ORDER_BIT;

        machineId = (genMachineId() << ORDER_DIGIT) & MACHINE_BIT;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 0, 0, 0, 0);
        START_TIME = getSecond(calendar.getTimeInMillis());

        CURRENT_TIME = getNow();
    }

    private SnowFlake() {
    }

    private static int genMachineId() {
        int macId;
        final Random random = new Random();
        try {
            byte[] address = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            macId = 0;
            for (byte b : address) {
                if(b < 0) {
                    macId += (-b) * random.nextInt(5);
                }
                else {
                    macId += b * random.nextInt(5);
                }
            }
        } catch (SocketException | UnknownHostException e) {
            macId = random.nextInt(1 << MACHINE_DIGIT);
        }

        return macId;
    }

    public static long generateId() {
        long time = getNow();
        int order;
        while (-1 == (order = genOrder(time))) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time = getNow();
        }
        return genTime(time) | machineId | order;
    }

    private static long getNow() {
        return getSecond(System.currentTimeMillis());
    }

    private static long getSecond(long millis) {
        return millis / 1000L;
    }

    /**
     * @param time
     * @return
     */
    private static long genTime(long time) {
        return ((time - START_TIME) << LOW_DIGIT) & TIME_BIT;
    }

    private static int genOrder(long time) {
        synchronized(SnowFlake.class) {
            if (time == CURRENT_TIME) {
                if(CURRENT_ORDER == ORDER_BIT) {
                    return -1;
                }
                return ++CURRENT_ORDER & ORDER_BIT;
            }

            if(time > CURRENT_TIME) {
                LAST_TIME = CURRENT_TIME;
                LAST_ORDER = CURRENT_ORDER;

                CURRENT_TIME = time;
                return (CURRENT_ORDER = 1);
            }

            if (time == LAST_TIME) {
                if(LAST_ORDER == ORDER_BIT) {
                    return -1;
                }
                return ++LAST_ORDER & ORDER_BIT;
            }

            return -1;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println((1 << LOW_DIGIT) - 1);

//        ExecutorService pool = Executors.newFixedThreadPool(300);
//
//        for (int i=0;i<1000;i++) {
//            pool.submit(new Work());
//        }
//
//        Thread.sleep(1000);
//        pool.shutdown();
    }

    public static class Work implements Runnable {

        @Override
        public void run() {
            System.out.println(SnowFlake.generateId());
        }
    }
}