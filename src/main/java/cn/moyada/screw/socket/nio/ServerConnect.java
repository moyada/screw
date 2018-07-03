package cn.moyada.screw.socket.nio;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-04-10 23:11
 */
public class ServerConnect implements AutoCloseable {

    private final Selector selector;
    private final ServerSocketChannel socketChannel;

    private final ThreadPoolExecutor threadPool;
    private final BeanPool<ByteBuffer> bufferBeanPool;

    public ServerConnect(int port) {
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
        this.threadPool = new ThreadPoolExecutor(0,
                Runtime.getRuntime().availableProcessors(),
                1L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>());
        this.bufferBeanPool = BeanPoolFactory.newPool(() -> ByteBuffer.allocateDirect(1024));
    }

    public void start(int timeoutMs) {
        timeoutMs = timeoutMs < 0 ? 0 : timeoutMs;
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iter;
        SelectionKey key;
        while(!Thread.interrupted()) {
            try {
                if(selector.select(timeoutMs) == 0){
                    continue;
                }
                System.out.println("wait");
                selectionKeys = selector.selectedKeys();
                iter = selectionKeys.iterator();
                while(iter.hasNext()){
                    key = iter.next();

                    if(!key.isValid()) {
                        continue;
                    }

                    iter.remove();

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
//            selectionKeys.clear();
            } catch (CancelledKeyException passExep) {
                System.out.println("channel close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socket;
        try {
            socket = serverChannel.accept();
            socket.configureBlocking(false);
            socket.register(key.selector(), SelectionKey.OP_READ, bufferBeanPool.allocate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        key.attach(ByteBuffer.allocateDirect(BUF_SIZE));
        InetAddress address = socket.socket().getInetAddress();
        System.out.println("Accepted connection from " + address.getHostAddress());
    }

    private void handleRead(SelectionKey key) {
        System.out.println("handle read");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer input = (ByteBuffer) key.attachment();
        int bytesRead = 0;
        while (true) {
            try {
                bytesRead = socketChannel.read(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead == 0) {
                // 客户端已经断开连接.
                try {
//                    key.attach(null);
//                    bufferBeanPool.recycle(input);
                    // 设置为下一次读取或是写入做准备
//                    key.interestOps(SelectionKey.OP_ACCEPT | SelectionKey.OP_WRITE);
//                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                    socketChannel.write(ByteBuffer.wrap("done".getBytes()));
                    // socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            threadPool.execute(() -> processRead(input));
        }
    }

    private void handleWrite(SelectionKey key) {
        System.out.println("handle write");
        threadPool.execute(() -> processWrite(key));
    }

    private void processRead(ByteBuffer input) {
//        ByteBuffer input = ByteBuffer.allocate(1024);
//        long bytesRead;
//        try {
            // 写数据到buffer
//            bytesRead = sc.read(input);
            // 切换buffer到读状态,内部指针归位.
            input.flip();
            String msg = Charset.forName("UTF-8").decode(input).toString();
            System.out.println("Server received [" + msg + "] ");//from client address:" + sc.getRemoteAddress());

            // echo back.
//            sc.write(ByteBuffer.wrap(msg.getBytes(Charset.forName("UTF-8"))));

            // 清空buffer
            input.clear();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    @Override
    public void close() throws IOException {
        if(selector!=null){
            selector.close();
        }
        if(socketChannel!=null){
            socketChannel.close();
        }
    }

    public static void main(String[] args) {
        new ServerConnect(5443).start(3000);
    }
}
