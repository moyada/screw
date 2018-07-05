package cn.moyada.screw.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author xueyikang
 * @create 2018-04-10 23:11
 */
public class Server implements AutoCloseable {
    private static final int BUF_SIZE=1024;
    private static final int PORT = 5443;
    private static final int TIMEOUT = 3000;

    private AsynchronousServerSocketChannel socketChannel;

    public static void main(String[] args) {
        new Server(PORT).run();
    }

    public Server(int port) {
        try{
            socketChannel = AsynchronousServerSocketChannel
                    .open().bind(new InetSocketAddress(port));
        }catch(IOException e){
            e.printStackTrace();
            try{
                close();
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }
    }

    public void run() {
        while(true) {
            try {
                runAccept(socketChannel.accept());
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void runAccept(Future<AsynchronousSocketChannel> accept) throws ExecutionException, InterruptedException, IOException {
        try (AsynchronousSocketChannel channel = accept.get()) {
            if ((channel != null) && (channel.isOpen())) {
                while (true) {
                    ByteBuffer buffer = ByteBuffer.allocate(32);
                    Future<Integer> readResult = channel.read(buffer);

                    // perform other computations

                    readResult.get();
                    System.out.println(new String(buffer.array()));

                    buffer.flip();
                    Future<Integer> writeResult = channel.write(buffer);

                    // perform other computations

                    writeResult.get();
                    buffer.clear();
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if(socketChannel!=null){
            socketChannel.close();
        }
    }

    class AsyncCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,Object> {

        private AsynchronousServerSocketChannel serverChannel;

        public AsyncCompletionHandler(AsynchronousServerSocketChannel serverChannel) {
            this.serverChannel = serverChannel;
        }

        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
            if (serverChannel.isOpen()){
                serverChannel.accept(null, this);
            }

            if ((clientChannel != null) && (clientChannel.isOpen())) {
                ReadWriteHandler handler = new ReadWriteHandler(clientChannel);
                ByteBuffer buffer = ByteBuffer.allocate(32);

                Map<String, Object> readInfo = new HashMap<>();
                readInfo.put("action", "read");
                readInfo.put("buffer", buffer);

                clientChannel.read(buffer, readInfo, handler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }

    class ReadWriteHandler implements CompletionHandler<Integer, Map<String, Object>> {

        private AsynchronousSocketChannel clientChannel;

        public ReadWriteHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer result, Map<String, Object> attachment) {
            Map<String, Object> actionInfo = attachment;
            String action = (String) actionInfo.get("action");

            if ("read".equals(action)) {
                ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
                buffer.flip();
                actionInfo.put("action", "write");

                clientChannel.write(buffer, actionInfo, this);
                buffer.clear();

            } else if ("write".equals(action)) {
                ByteBuffer buffer = ByteBuffer.allocate(32);

                actionInfo.put("action", "read");
                actionInfo.put("buffer", buffer);

                clientChannel.read(buffer, actionInfo, this);
            }
        }

        @Override
        public void failed(Throwable exc, Map<String, Object> attachment) {
            //
        }
    }
}
