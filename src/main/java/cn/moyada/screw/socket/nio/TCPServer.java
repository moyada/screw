package cn.moyada.screw.socket.nio;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-04-10 23:11
 */
public class TCPServer implements AutoCloseable {

    private final Selector selector;
    private final ServerSocketChannel socketChannel;

    private final ThreadPoolExecutor threadPool;
    private final BeanPool<ByteBuffer> bufferBeanPool;
    private final BeanPool<CharBuffer> charBeanPool;
    private final BeanPool<StringBuilder> stringBeanPool;

    private final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public TCPServer(int port) {
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
        this.bufferBeanPool = BeanPoolFactory.newConcurrentPool(() -> ByteBuffer.allocateDirect(1024));
        this.charBeanPool = BeanPoolFactory.newConcurrentPool(() -> CharBuffer.allocate(1024));
        this.stringBeanPool = BeanPoolFactory.newConcurrentPool(() -> new StringBuilder(1024));
    }

    public void start(int timeoutMs) {
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
            SocketChannel socketChannel = (SocketChannel) key.channel();

            handleRead(socketChannel);
//            threadPool.execute(() -> handleRead(socketChannel));
        }
        else if(key.isWritable()){
            handleWrite(key);
        }
    }

    private void handleAccept(SelectionKey key) {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socket;
        try {
            socket = serverChannel.accept();
            socket.configureBlocking(false);
            socket.register(key.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InetAddress address = socket.socket().getInetAddress();
        System.out.println("Accepted connection from " + address.getHostAddress());
    }

    private void handleRead(SocketChannel socketChannel) {
        System.out.println("handle read");

//        ByteBuffer input = (ByteBuffer) key.attachment();
        StringBuilder buf = stringBeanPool.allocate();
        ByteBuffer input = bufferBeanPool.allocate();
        CharBuffer charBuffer = charBeanPool.allocate();

        char data;
        int bytesRead;
        while (true) {
            try {
                bytesRead = socketChannel.read(input);
                if(bytesRead == 0) {
                    break;
                }

                input.flip();
                decoder.decode(input, charBuffer, false);
                charBuffer.flip();
                bytesRead = charBuffer.length();

                for (int index = 0; index < bytesRead; index++) {
                    data = charBuffer.get(index);
                    if(data == '\n') {
                        processRead(socketChannel, buf.toString());
                        buf.delete(0, index);
                        continue;
                    }
                    buf.append(data);
                }

                input.clear();
                charBuffer.clear();
            } catch (IOException e) {
                // e.printStackTrace();
                input.clear();
                bufferBeanPool.recycle(input);
                buf.setLength(0);
                stringBeanPool.recycle(buf);
                charBuffer.clear();
                charBeanPool.recycle(charBuffer);
                return;
            }
        }

        input.clear();
        bufferBeanPool.recycle(input);
        stringBeanPool.recycle(buf);
        charBuffer.clear();
        charBeanPool.recycle(charBuffer);
    }

    private void handleWrite(SelectionKey key) {
        System.out.println("handle write");
        threadPool.execute(() -> processWrite(key));
    }

    private void processRead(SocketChannel socketChannel, String msg) {
        System.out.println("Server received [" + msg + "] ");//from client address:" + sc.getRemoteAddress());

        callbackRead(socketChannel, msg + " done.");
    }

    private void callbackRead(SocketChannel socketChannel, String msg) {
        // echo back.
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            // e.printStackTrace();
        }
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
        new TCPServer(5443).start(3000);
    }
}
