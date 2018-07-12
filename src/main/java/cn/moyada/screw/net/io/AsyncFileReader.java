package cn.moyada.screw.net.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xueyikang
 * @create 2018-03-25 23:55
 */
public class AsyncFileReader {

    private static final int BUF_SIZE = 102;

    private final AtomicInteger currentOrder;
    private final Map<Integer, Thread> threadMap;
    private volatile boolean running;

    private int posIndex, remainIndex;
    private long limit;
    private List<String> data;
    private byte[] remain;
    private AsynchronousFileChannel channel;

    public AsyncFileReader(String fileName) {
        Path path = Paths.get(fileName);
        if(Files.notExists(path)) {
            throw cn.moyada.screw.exception.IOException.FILE_NOT_FOUNT;
        }

        try {
            this.channel = AsynchronousFileChannel.open(
                    path, StandardOpenOption.READ);
            this.limit = channel.size();
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
        this.data = new ArrayList<>();
        this.posIndex = 0;
        this.remainIndex = -1;
        this.remain = new byte[BUF_SIZE];
        this.currentOrder = new AtomicInteger(0);
        this.threadMap = new HashMap<>();
        this.running = true;
        read();
        LockSupport.parkNanos("init", 1000);
    }

    private void read() {
        FileCompletionHandler completionHandler = new FileCompletionHandler();
        ByteBuffer buffer;
        for (int index = 0, order = 0; index < limit; index = index + BUF_SIZE, order++) {
            buffer = ByteBuffer.allocateDirect(BUF_SIZE);
            channel.read(buffer, index, new OrderNode(order, buffer), completionHandler);
        }
    }

    public boolean hasNext() {
        return posIndex < data.size() || this.running;
    }

    public String nextLine() {
        while (running || data.size() <= posIndex) {
            LockSupport.parkNanos(posIndex, 100);
        }
        return data.get(posIndex++);
    }

    class OrderNode {

        private int order;

        private ByteBuffer byteBuffer;

        public OrderNode(int order, ByteBuffer byteBuffer) {
            this.order = order;
            this.byteBuffer = byteBuffer;
        }
    }

    class FileCompletionHandler implements CompletionHandler<Integer, OrderNode> {

        @Override
        public void completed(Integer result, OrderNode attachment) {

            if(!currentOrder.compareAndSet(attachment.order, attachment.order)) {
                threadMap.put(attachment.order, Thread.currentThread());
                LockSupport.park(attachment);
            }

            byte b;
            int currentIndex = remainIndex + 1;
            for (int index = 0; index < result; currentIndex++, index++) {
                b = attachment.byteBuffer.get(index);
                if(b == '\n') {
                    data.add(new String(remain, 0, currentIndex));
                    currentIndex = -1;
                    continue;
                }
                remain[currentIndex] = b;
            }
            remainIndex = currentIndex;

            while (!currentOrder.compareAndSet(attachment.order, attachment.order + 1)) {

            }
            Thread thread = threadMap.get(attachment.order + 1);
            if(null == thread) {
                data.add(new String(remain, 0, currentIndex));
                running = false;
            }
            else {
                LockSupport.unpark(thread);
            }
        }

        @Override
        public void failed(Throwable exc, OrderNode attachment) {
            exc.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AsyncFileReader reader = new AsyncFileReader("/Users/xueyikang/screw/test.txt");
        Thread.sleep(1000);
        int i = 0;
        while (reader.hasNext()) {
            System.out.println(reader.nextLine());
            i++;
        }
        if(i < 288) {
            System.out.println(i);
        }
    }
}
