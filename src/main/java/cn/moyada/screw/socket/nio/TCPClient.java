package cn.moyada.screw.socket.nio;

import cn.moyada.screw.utils.StringUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author xueyikang
 * @create 2018-04-10 23:02
 */
public class TCPClient implements AutoCloseable {

    private final ByteBuffer writeBuffer;
    private final ByteBuffer readBuffer;

    private final SocketChannel socketChannel;

    private final Selector selector;

    public TCPClient(String host, int port) {
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
        writeBuffer = ByteBuffer.allocateDirect(2048);
        readBuffer = ByteBuffer.allocateDirect(2048);
        System.out.println("Client start");

        new Thread(() -> {
            try {
                listener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void send(String msg) {
        try {
            if(!socketChannel.finishConnect()) {
                throw new ConnectException("client finish connecting not yet.");
            }
            writeBuffer.clear();
            writeBuffer.put(decode(msg));
            writeBuffer.flip();
            while(writeBuffer.hasRemaining()){
                socketChannel.write(writeBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] decode(String msg) {
        return (msg + '\n').getBytes(StandardCharsets.UTF_8);
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
        int readIndex = channel.read(readBuffer);
        if(0 == readIndex) {
            return;
        }
        readBuffer.flip();
        byte[] bytes = new byte[readIndex];
        readBuffer.get(bytes, 0, readIndex);

        System.out.println(" 客户端收到信息：" + new String(bytes, 0, readIndex));
        readBuffer.clear();
    }

    @Override
    public void close() throws IOException {
        if (socketChannel != null && socketChannel.isConnected()){
            socketChannel.close();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner sc=new Scanner(System.in);

        System.out.print("请输入ip：");
        String in = sc.nextLine();

        String host;
        if(StringUtil.isEmpty(in)) {
            host = "127.0.0.1";
        }
        else {
            if (!checkHost(in)) {
                throw new IllegalArgumentException("host error");
            }
            host = in;
        }

        System.out.print("请输入端口：");

        int port = sc.nextInt();
        if(port < 1000) {
            throw new IllegalArgumentException("port must be bigger than 1000");
        }

        TCPClient client = new TCPClient(host, port);

        System.out.print("请输入：");
        while (true) {
            in = sc.nextLine();
            if(StringUtil.isEmpty(in)) {
                continue;
            }
            if(in.equals("exit")) {
                break;
            }

            client.send(in);
        }

        TimeUnit.SECONDS.sleep(1L);
        client.close();
        Runtime.getRuntime().exit(1);
    }

    private static boolean checkHost(String host) {
        String[] split = host.split("\\.");
        if(split.length != 4) {
            return false;
        }

        for (String s : split) {
            try {
                Integer.valueOf(s);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
