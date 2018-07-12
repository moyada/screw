package cn.moyada.screw.net.socket.nio;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;
import cn.moyada.screw.utils.AssertUtil;
import cn.moyada.screw.utils.StringUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xueyikang
 * @create 2018-04-10 23:11
 */
public class TCPServer implements Closeable {
    private static int DEFAULT_BUF_SIZE = 1024;

    private final Selector selector;
    private final ServerSocketChannel socketChannel;

    private final ExecutorService threadPool;
    private final BeanPool<ByteBuffer> bufferBeanPool;
    private final BeanPool<byte[]> btyesBeanPool;

//    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public TCPServer(int port, int size) {
        try {
            socketChannel = ServerSocketChannel.open();
            socketChannel.socket().bind(new InetSocketAddress(port));
            socketChannel.configureBlocking(false);
            // 接收连接事件
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 设置事件处理器
            System.out.println("Server start.");
        } catch(IOException e){
            e.printStackTrace();
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        this.threadPool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
//        this.threadPool = new ThreadPoolExecutor(1,
//                Runtime.getRuntime().availableProcessors(),
//                1L, TimeUnit.MINUTES,
//                new LinkedBlockingQueue<>());

        this.bufferBeanPool = BeanPoolFactory.newConcurrentPool(size, () -> ByteBuffer.allocateDirect(DEFAULT_BUF_SIZE));
        this.btyesBeanPool = BeanPoolFactory.newConcurrentPool(size, () -> new byte[DEFAULT_BUF_SIZE]);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start(long timeoutMs) {
        timeoutMs = timeoutMs < 0 ? 0 : timeoutMs;
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iter;
        SelectionKey key;
        while(true) {
            try {
                if(selector.select(timeoutMs) == 0){
                    continue;
                }
                selectionKeys = selector.selectedKeys();
                iter = selectionKeys.iterator();
                while(iter.hasNext()){
                    key = iter.next();
                    if(!key.isValid()) {
                        continue;
                    }

                    iter.remove();

                    dispatch(key);
                }
            } catch (CancelledKeyException passExep) {
                System.out.println("channel close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey key) {
        if(key.isAcceptable()){
            handleAccept(key);
        }
        else if(key.isReadable()){
            handleRead(key);
        }
        else if(key.isWritable()){
            handleWrite(key);
        }
    }

    private void handleAccept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socket;
        String address;
        try {
            socket = serverChannel.accept();
            socket.configureBlocking(false);
            address = socket.getRemoteAddress().toString();
            socket.register(key.selector(), SelectionKey.OP_READ, address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Accepted connection from " + address);
//        InetAddress address = socket.socket().getInetAddress();
//        System.out.println("Accepted connection from " + address.getHostAddress());
    }

    private void handleRead(SelectionKey key) {
        System.out.println("handle read");
        SocketChannel socketChannel = (SocketChannel) key.channel();

        List<ByteBuffer> bufferList = new ArrayList<>();
        int bytesRead;
        ByteBuffer input;

        while (true) {
            System.out.println("read");
            if(!socketChannel.isOpen()) {
                System.out.println("88");
                key.interestOps(SelectionKey.OP_ACCEPT);
            }
            if(!socketChannel.isConnected()) {
                System.out.println("88");
                key.interestOps(SelectionKey.OP_ACCEPT);
            }

            input = bufferBeanPool.allocate();
            try {
                bytesRead = socketChannel.read(input);
                if (bytesRead == -1) {
                    key.cancel();
                    socketChannel.close();
                    clearByteBuffer(bufferList);
                    return;
                }
                if (bytesRead == 0) {
                    break;
                }
                bufferList.add(input);
            } catch (IOException e) {
                // e.printStackTrace();
                clearByteBuffer(bufferList);
                return;
            }
        }

        if(bufferList.isEmpty()) {
            return;
        }

        threadPool.execute(() -> handleSocket(socketChannel, bufferList));
    }

    private void handleSocket(SocketChannel socketChannel, List<ByteBuffer> bufferList) {
        byte[] bytes = btyesBeanPool.allocate();

        int bytesRead, start;
        String msg;

        for (ByteBuffer input : bufferList) {
            input.flip();

            bytesRead = input.limit();

            // if array can not be contain
//            if(bytes.length < bytesRead) {
//                int finalBytesRead = bytesRead;
//                bytes = btyesBeanPool.allocate(() -> new byte[finalBytesRead]);
//            }
            input.get(bytes, 0, bytesRead);

            start = 0;
            for (int index = 0; index < bytesRead; index++) {
                if(bytes[index] == '\n') {

                    msg = processRead(new String(bytes, start, index));

                    callbackRead(socketChannel, input, msg);

                    start = index+1;
                }
            }
        }
        clearByteBuffer(bufferList);
        btyesBeanPool.recycle(bytes);
    }

    private String processRead(String input) {
        System.out.println("Server received [" + input + "] ");

        // ... do some process

        return input + " done.";
    }

    private void callbackRead(SocketChannel socketChannel, ByteBuffer buf, String msg) {
        buf.clear();
        buf.put(msg.getBytes(StandardCharsets.UTF_8));
        buf.flip();
        // echo back.
        try {
            socketChannel.write(buf);
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    private void handleWrite(SelectionKey key) {
        System.out.println("handle write");
        threadPool.execute(() -> processWrite(key));
    }

    private void processWrite(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.put("hi - from non-blocking server".getBytes());
        try {
            client.write(buffer);
            // switch to read, and disable write,
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearByteBuffer(List<ByteBuffer> bufferList) {
        if(bufferList.isEmpty()) {
            return;
        }
        bufferList.forEach(buffer -> {
            buffer.clear();
            bufferBeanPool.recycle(buffer);
        });
    }

    @Override
    public void close() throws IOException {
        if(selector != null && selector.isOpen()){
            selector.close();
        }
        if(socketChannel != null && socketChannel.isOpen()){
            socketChannel.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.print("请输入端口：");

        int port = sc.nextInt();
        AssertUtil.checkPort(port);

        System.out.print("请输入缓冲池大小：");
        String in = sc.next();
        in = sc.nextLine();
        int size;

        if(StringUtil.isEmpty(in)) {
            size = 5;
        }
        else {
            try {
                size = Integer.valueOf(in);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("size need a number");
            }
            if (size < 1) {
                throw new IllegalArgumentException("size must be a positive number");
            }
        }
        TCPServer server = new TCPServer(port, size);
        server.start(1000L);


        while ((sc.nextLine()).equals("exit")) {
            server.close();
        }
    }
}
