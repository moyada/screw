package cn.moyada.screw.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-04-10 23:11
 */
public class Server implements AutoCloseable {

    private final AsynchronousServerSocketChannel serverSocketChannel;

    public Server(int port) throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel
                .open().bind(new InetSocketAddress(port));
    }

    public void start() {
        serverSocketChannel.accept(new HashMap<>(), new ReadCompletionHandler());
    }

    class ReadCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Map<String, Object>> {

        @Override
        public void completed(AsynchronousSocketChannel socketChannel, Map<String, Object> attachment) {
            if ((socketChannel != null) && (socketChannel.isOpen())) {
                attachment.put("action", "read");

                ByteBuffer buffer;
                if(attachment.containsKey("buffer")) {
                    buffer = (ByteBuffer) attachment.get("buffer");
                    buffer.clear();
                }
                else {
                    buffer = ByteBuffer.allocate(1024);
                    attachment.put("buffer", buffer);
                }

                socketChannel.read(buffer, attachment, new WriteReadHandler(socketChannel));

                serverSocketChannel.accept(attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, Map<String, Object> attachment) {
            System.out.println("read error.");
        }
    }

    class WriteReadHandler implements CompletionHandler<Integer, Map<String, Object>> {

        private AsynchronousSocketChannel clientChannel;

        public WriteReadHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer result, Map<String, Object> attachment) {
            String action = (String) attachment.get("action");

            if ("read".equals(action)) {
                if(result < 1) {
                    System.out.println("read action occur unknow error.");
                    return;
                }

                ByteBuffer buffer = (ByteBuffer) attachment.get("buffer");

                byte[] bytes = new byte[result];
                buffer.get(bytes, 0, result);
                System.out.println("accept: " + new String(bytes, 0, result));

                buffer.clear();
                attachment.put("action", "write");
                attachment.remove("buffer");
                buffer.flip();

                clientChannel.write(buffer, attachment, this);
                buffer.clear();

            } else if ("write".equals(action)) {
                System.out.println("write completed");
            }
        }

        @Override
        public void failed(Throwable exc, Map<String, Object> attachment) {
            System.out.println("callback error.");
        }
    }

    @Override
    public void close() throws IOException {
        if(serverSocketChannel != null && serverSocketChannel.isOpen()){
            serverSocketChannel.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Server(5666).start();

        Thread.currentThread().join();
    }
}
