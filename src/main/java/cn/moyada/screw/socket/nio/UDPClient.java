package cn.moyada.screw.socket.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
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
    private InetSocketAddress address;

    private final DatagramChannel channel;
    private final Selector selector;

    private final ByteBuffer buf = ByteBuffer.allocate(2048);// java里一个(utf-8)中文3字节,gbk中文占2个字节

    public UDPClient(String host, int port) throws IOException {
        selector = Selector.open();
        channel = DatagramChannel.open();
        address = new InetSocketAddress(host, port);
        System.out.println("客户器启动");
    }

    /* 编码过程 */
    public void send(String str) {
        try {
            channel.configureBlocking(false);
            channel.connect(address);// 连接服务端
            channel.write(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 服务器服务方法 */
    public void listener() {
        new Thread(this::dispatch).start();
    }

    private void dispatch() {
        SelectionKey key;
        while(true) {
            try {
                if (selector.select(3000L) > 0) {
                    /* 得到已经被捕获了的SelectionKey的集合 */
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();

                        if (key.isReadable()) {
                            receive(key);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* 接收 */
    private void receive(SelectionKey key) throws IOException {
        // ：接收时需要考虑字节长度
        DatagramChannel sc = (DatagramChannel) key.channel();
        StringBuilder content = new StringBuilder();
        try {
            buf.clear();
            SocketAddress address = sc.receive(buf); // read into buffer.

            buf.flip(); // make buffer ready for read
            int limit = buf.limit();
            byte[] bytes = new byte[limit];
            while (buf.hasRemaining()) {
                buf.get(bytes, 0, limit);
                content.append(new String(bytes, 0, limit));
            }

            System.out.println("接收：" + address.toString() + " - " + content.toString());

        } catch (PortUnreachableException ex) {
            System.out.println("服务端端口未找到!");
        }
    }

    @Override
    public void close() throws IOException {
        if (null != channel && channel.isOpen()) {
            channel.close();
        }

        if (null != selector && selector.isOpen()) {
            selector.close();
        }
    }

    public static void main(String[] args) throws IOException {
        UDPClient udpClient = new UDPClient("127.0.0.1", 5555);
        udpClient.listener();
        udpClient.send("hello");
    }
}
