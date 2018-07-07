package cn.moyada.screw.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author xueyikang
 * @create 2018-04-11 11:19
 */
public class Client implements AutoCloseable {

    private final AsynchronousSocketChannel socketChannel;

    private volatile boolean connected;

    public Client(String host, int port) throws IOException {
        final InetSocketAddress address = new InetSocketAddress(host, port);
        socketChannel = AsynchronousSocketChannel.open();
        connected = false;
        socketChannel.connect(address, address, new ConnectCompleteHandler());
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(String message) {
        byte[] byteMsg = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        socketChannel.write(buffer, null, new WriteCompleteHandler());
    }

    public void listenner() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer, null, new ReadCompleteHandler(buffer));
    }

    class ConnectCompleteHandler implements CompletionHandler<Void, InetSocketAddress> {

        private int count = 0;

        @Override
        public void completed(Void result, InetSocketAddress address) {
            connected = true;
            System.out.println("connected success.");
        }

        @Override
        public void failed(Throwable exc, InetSocketAddress address) {
            if(count > 10) {
                System.out.println("connected failed");
            }
            count++;
            socketChannel.connect(address, address, this);
        }
    }

    class WriteCompleteHandler implements CompletionHandler<Integer, Object> {

        @Override
        public void completed(Integer result, Object attachment) {
            System.out.println("write completed.");
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("write failed.");
        }
    }

    class ReadCompleteHandler implements CompletionHandler<Integer, Object> {

        private final ByteBuffer buffer;

        public ReadCompleteHandler(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            byte[] bytes = new byte[result];
            buffer.get(bytes, 0, result);
            buffer.clear();

            System.out.println("read: " + new String(bytes, 0, result));
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("read failed.");
        }
    }

    @Override
    public void close() throws Exception {
        if(socketChannel != null && socketChannel.isOpen()){
            socketChannel.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client("127.0.0.1", 5666);
        while (!client.isConnected()) {
            Thread.sleep(100);
        }
        client.send("hi");
        client.send("88");

        Thread.currentThread().join();
    }
}
