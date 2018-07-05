package cn.moyada.screw.socket.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author xueyikang
 * @create 2018-04-11 11:19
 */
public class Client implements AutoCloseable {

    private AsynchronousSocketChannel socketChannel;

    public Client(String host, int port) {
        try {
            socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
        }
        catch (IOException e) {
            e.printStackTrace();
            try {
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public String sendMessage(String message) throws ExecutionException, InterruptedException {
        byte[] byteMsg = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(byteMsg);
        Future<Integer> writeResult = socketChannel.write(buffer);

        // do some computation

        writeResult.get();
        buffer.flip();
        Future<Integer> readResult = socketChannel.read(buffer);

        // do some computation

        readResult.get();
        String echo = new String(buffer.array()).trim();
        buffer.clear();
        return echo;
    }

    @Override
    public void close() throws Exception {
        if(socketChannel!=null){
            socketChannel.close();
        }
    }
}
