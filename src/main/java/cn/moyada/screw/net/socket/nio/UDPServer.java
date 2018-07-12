package cn.moyada.screw.net.socket.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author xueyikang
 * @create 2018-04-11 14:06
 */
public class UDPServer implements Closeable {

    private final DatagramChannel channel;

    private final Selector selector;

    private final ByteBuffer byteBuffer;

    public UDPServer(int port) throws IOException {
        // 打开一个UDP Channel
        channel = DatagramChannel.open();

        // 设定为非阻塞通道
        channel.configureBlocking(false);
        // 绑定端口
        channel.socket().bind(new InetSocketAddress(port));

        // 打开一个选择器
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        byteBuffer = ByteBuffer.allocate(2000);
    }

    public void start() {
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iter;
        SelectionKey key;
        while (true) {
            try
            {
                if (selector.select(2000L) == 0) {
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

                    if(key.isReadable()) {
                        DatagramChannel datagramChannel = (DatagramChannel) key
                                .channel();

                        byteBuffer.clear();
                        // 读取
                        InetSocketAddress address = (InetSocketAddress) datagramChannel
                                .receive(byteBuffer);

                        System.out.println(new String(byteBuffer.array()));

                        // 删除缓冲区中的数据
                        byteBuffer.clear();

                        String message = "data come from server";

                        byteBuffer.put(message.getBytes());

                        byteBuffer.flip();

                        // 发送数据
                        datagramChannel.send(byteBuffer, address);
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void close() throws IOException {
        if(null != channel && channel.isOpen()) {
            channel.close();
        }

        if(null != selector && selector.isOpen()) {
            selector.close();
        }
    }

    public static void main(String[] args) throws IOException {
        new cn.moyada.screw.net.socket.bio.UDPServer(5555).start();
    }
}
