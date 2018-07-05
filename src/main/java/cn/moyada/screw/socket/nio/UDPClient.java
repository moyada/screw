package cn.moyada.screw.socket.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author xueyikang
 * @create 2018-04-11 14:06
 */
public class UDPClient implements Closeable {

    // UDP协议客户端
    private String serverIp = "127.0.0.1";
    private int port = 9975;
    // private ServerSocketChannel serverSocketChannel;
    private final DatagramChannel channel;
    private final Selector selector;

    private final ByteBuffer buf = ByteBuffer.allocate(2048);// java里一个(utf-8)中文3字节,gbk中文占2个字节

    public UDPClient(String host, int port) throws IOException {
        selector = Selector.open();
        channel = DatagramChannel.open();
        System.out.println("客户器启动");
    }

    /* 编码过程 */
    public void send(String str) {
        try {
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(serverIp, port));// 连接服务端
            channel.write(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 服务器服务方法 */
    public void listener() throws IOException {
        /** 外循环，已经发生了SelectionKey数目 */
        while (selector.select() > 0) {
            /* 得到已经被捕获了的SelectionKey的集合 */
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        receive(key);
                    }
                    if (key.isWritable()) {
                        // send(key);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        key.cancel();
                        key.channel().close();
                    } catch (ClosedChannelException cex) {
                        e.printStackTrace();
                    }
                }
            }
            /* 内循环完 */
        }
        /* 外循环完 */
    }

    /* 接收 */
    synchronized public void receive(SelectionKey key) throws IOException {
        String threadName = Thread.currentThread().getName();
        if (key == null)
            return;
        try {
            // ***用channel.receive()获取消息***//
            // ：接收时需要考虑字节长度
            DatagramChannel sc = (DatagramChannel) key.channel();
            StringBuilder content = new StringBuilder();
            //第一次接；udp采用数据报模式，发送多少次，接收多少次

            SocketAddress address = sc.receive(buf); // read into buffer.
            System.out.println(threadName + "\t" + address.toString());
            buf.flip(); // make buffer ready for read

            byte[] bytes = new byte[buf.limit()];
            while (buf.hasRemaining()) {
                buf.get(bytes, 0, buf.limit());
                content.append(new String(bytes, 0, buf.limit()));
            }
            buf.clear(); // make buffer ready for writing次
            System.out.println("接收：" + address.toString() + " - " + content.toString().trim());

        } catch (PortUnreachableException ex) {
            System.out.println(threadName + "服务端端口未找到!");
        }
        send(2);
    }

    boolean flag = false;

    public void send(int i) {
        if (flag)
            return;
        try {
            // channel.write(ByteBuffer.wrap(new String("客户端请求获取消息(第"+i+"次)").getBytes()));
            // channel.register(selector, SelectionKey.OP_READ );
            ByteBuffer buf2 = ByteBuffer.allocate(48);
            buf2.clear();
            buf2.put(("客户端请求获取消息(第" + i + "次)").getBytes());
            buf2.flip();
            channel.write(buf2);
            channel.register(selector, SelectionKey.OP_READ );
//			int bytesSent = channel.send(buf2, new InetSocketAddress(serverIp,port)); // 将消息回送给服务端
        } catch (IOException e) {
            e.printStackTrace();
        }
        flag = true;
    }

    int y = 0;

    public void send(SelectionKey key) {
        if (key == null)
            return;
        // ByteBuffer buff = (ByteBuffer) key.attachment();
        DatagramChannel sc = (DatagramChannel) key.channel();
        try {
            sc.write(ByteBuffer.wrap(new String("aaaa").getBytes()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("send2() " + (++y));
    }
}
