package cn.moyada.screw.socket.nio;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-04-10 23:02
 */
public class TCPClient implements AutoCloseable {

    private ByteBuffer writeBuffer;
    private ByteBuffer readBuffer;

    private final SocketChannel socketChannel;

    private final Selector selector;

    public TCPClient(String host, int port) {
        writeBuffer = ByteBuffer.allocate(1024);
        readBuffer = ByteBuffer.allocate(1024);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        System.out.println("Client start");

        new Thread(() -> {
            try {
                listener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void write(String msg) {
        try {
            if(!socketChannel.finishConnect()) {
                throw new ConnectException("client finish connecting not yet.");
            }
            writeBuffer.clear();
            writeBuffer.put(msg.getBytes(StandardCharsets.UTF_8));
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()){
                socketChannel.write(writeBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listener() throws IOException {
        Iterator<SelectionKey> iter;
        SelectionKey key;
        while (true) {
            //选择一组可以进行I/O操作的事件，放在selector中,客户端的该方法不会阻塞，
            //这里和服务端的方法不一样，查看api注释可以知道，当至少一个通道被选中时，
            //selector的wakeup方法被调用，方法返回，而对于客户端来说，通道一直是被选中的
            if(selector.select() == 0){
                continue;
            }

            // 获得selector中选中的项的迭代器
            iter = selector.selectedKeys().iterator();
//            System.out.println("wait");

            while (iter.hasNext()) {
                key = iter.next();

                // 删除已选的key,以防重复处理
                iter.remove();

                if (key.isReadable()) {
                    System.out.println("read");
                    read(key);
                }
            }
        }
    }

    /**
     * 处理读取服务端发来的信息的事件
     * @param key
     * @throws IOException
     */
    public void read(SelectionKey key)throws IOException{
        // 服务器可读取消息:得到事件发生的Socket通道
        SocketChannel channel = (SocketChannel)key.channel();

        // 创建读取的缓冲区
        channel.read(readBuffer);
        byte[] data = readBuffer.array();
        String msg = new String(data).trim();
        System.out.println(" 客户端收到信息："+msg);
        readBuffer.clear();
    }

    @Override
    public void close() throws IOException {
        if(socketChannel!=null){
            socketChannel.close();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
//        new Thread(() -> {
//            TCPClient client1 = new TCPClient("127.0.0.1", 5443);
//            client1.write("haha");
////            client1.read();
//        }).start();

        TCPClient client = new TCPClient("127.0.0.1", 5443);

        for (int i = 0; i < 10; i++) {
            client.write("hi哈 " + i + " \n");
            TimeUnit.MILLISECONDS.sleep(10);
        }
        System.out.println("second");
        client.write("hggggggggei\n");
        TimeUnit.SECONDS.sleep(5);
//        client.close();
//        Thread.currentThread().join();
//        client.read();
//        client.write("6666");
//        client.write("laotie");
    }
}
