package cn.moyada.screw.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xueyikang
 * @create 2018-02-08 17:17
 */
public final class SnowFlake {

    private static int CURRENT_ORDER = 0;

    private static int LAST_ORDER;

    private static final long START_TIME;

    private static long CURRENT_TIME;

    private static long LAST_TIME = 0;

    private static final int machineId;

    private static final long TIME_BIT;

    private static final int MACHINE_BIT;

    private static final int ORDER_BIT;

    private static final int MACHINE_DIGIT = 10;

    private static final int ORDER_DIGIT = 12;

    private static final int LOW_DIGIT = MACHINE_DIGIT + ORDER_DIGIT;

    static {
        // 2^63 - 1 => 1*63bit
        long all = Long.MAX_VALUE;
        // 2^31 - 1 - 2^9 = 2^22 - 1 => 1*22bit
        int low = Integer.MAX_VALUE >>> (MACHINE_DIGIT - 1);

        // high 41 bit(1~42) of 64
        TIME_BIT = all ^ low;
        // low 12 bit(53~64) of 64
        ORDER_BIT = low >>> MACHINE_DIGIT;
        // middle 10 bit(43~52) of 64
        MACHINE_BIT = ORDER_BIT << ORDER_DIGIT;

        try {
            machineId = (genMachineId() << ORDER_DIGIT) & MACHINE_BIT;
        } catch (UnknownHostException | SocketException e) {
             throw new RuntimeException(e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);

        START_TIME = calendar.getTimeInMillis();
        CURRENT_TIME = System.currentTimeMillis();
    }

    private SnowFlake() {
    }

    private static int genMachineId() throws UnknownHostException, SocketException {
        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
        if(null == mac) {
            return 0;
        }

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

    private static final long getNow() {
        return System.currentTimeMillis();
    }

    /**
     * generate time start with 2000 year
     * @param time
     * @return
     */
    private static final long genTime(long time) {
        return ((time - START_TIME) << LOW_DIGIT) & TIME_BIT;
    }

    private static final int genOrder(long time) {
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

        ExecutorService pool = Executors.newFixedThreadPool(300);

        for (int i=0;i<1000;i++) {
            pool.submit(new Work());
        }

        Thread.sleep(1000);
        pool.shutdown();
    }

    public static class Work implements Runnable {

        @Override
        public void run() {
            System.out.println(SnowFlake.generateId());
        }
    }

    public class NetClassLoader extends ClassLoader {

        

    }
}