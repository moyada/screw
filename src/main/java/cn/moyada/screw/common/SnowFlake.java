package cn.moyada.screw.common;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;

/**
 * @author xueyikang
 * @create 2018-02-08 17:17
 */
public class SnowFlake {

    private static int order = 0;

    private static long now;

    private static final int machineId;

    private static long TIME_BIT;

    private static int MACHINE_BIT;

    private static int ORDER_BIT;

    private static int MACHINE_DIGIT = 10;

    private static int ORDER_DIGIT = 12;

    private static int LOW_DIGIT = MACHINE_DIGIT + ORDER_DIGIT;

    static {
        // 63
        long all = Long.MAX_VALUE;
        // 22
        int low = Integer.MAX_VALUE >>> 9;

        TIME_BIT = all ^ low;
        ORDER_BIT = low >>> 10;
        MACHINE_BIT = ORDER_BIT << ORDER_DIGIT;

        try {
            machineId = (getMachineId() << ORDER_DIGIT) & MACHINE_BIT;
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }

        now = Instant.now().toEpochMilli() << LOW_DIGIT & TIME_BIT;
    }

    private SnowFlake() {
    }

    private static int getMachineId() throws UnknownHostException, SocketException {
        byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();

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

    public static long getSnowFlakeId() {
        long time = getTime();
        int order = getOrder(time);
        return time + machineId + order;
    }

    private static final long getNow() {
        return Instant.now().toEpochMilli();
    }

    private static final long getTime() {
        return (getNow() << LOW_DIGIT) & TIME_BIT;
    }

    private static final int getOrder(long time) {
        synchronized(SnowFlake.class) {
            if (time == now) {
                return ++order & ORDER_BIT;
            }
            now = time;
            order = 1;
            return order;
        }
    }
}