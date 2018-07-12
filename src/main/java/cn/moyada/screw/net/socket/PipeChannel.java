package cn.moyada.screw.net.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @author xueyikang
 * @create 2018-04-11 14:08
 */
public class PipeChannel implements AutoCloseable {

    private final Pipe.SinkChannel sinkChannel;
    private final Pipe.SourceChannel sourceChannel;

    private final ByteBuffer readBuf;
    private final ByteBuffer writeBuf;
    private final byte[] bytes;

    private final static int DEFAULT_SIZE = 1024;

    public PipeChannel() throws IOException {
        Pipe pipe = Pipe.open();
        this.sinkChannel = pipe.sink();
        this.sourceChannel = pipe.source();//向通道中读数据
        this.readBuf = ByteBuffer.allocate(DEFAULT_SIZE);
        this.writeBuf = ByteBuffer.allocate(DEFAULT_SIZE);
        bytes = new byte[DEFAULT_SIZE];
    }

    private void write(String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        int length = bytes.length;

        int size, pos;
        for (int index = 0; index < length; index = index + DEFAULT_SIZE) {
            pos = index * DEFAULT_SIZE;

            writeBuf.clear();

            size = length - pos < DEFAULT_SIZE ? length - pos : DEFAULT_SIZE;

            writeBuf.put(bytes, pos, size);
            writeBuf.flip();

            while(writeBuf.hasRemaining()){
                System.out.println("write: " + writeBuf);
                sinkChannel.write(writeBuf);
            }
        }
    }

    private String read() throws IOException {

        int bytesRead;
//        while(0 < (bytesRead = sourceChannel.read(readBuf))){
        if(0 < (bytesRead = sourceChannel.read(readBuf))){
            StringBuilder msg = new StringBuilder();
            readBuf.flip();
//            int i=0;

            while(readBuf.hasRemaining()){
                readBuf.get(bytes, 0, bytesRead);
//                b[i]=readBuf.get();
//                i++;
            }
            msg.append(new String(bytes, 0, bytesRead));

            readBuf.clear();
            return msg.toString();
        }
        else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        sinkChannel.close();
        sourceChannel.close();
    }

    public static void main(String[] args) throws IOException {
        PipeChannel channel = new PipeChannel();
        for(int index = 0; index < 100; index++ ) {
            channel.write("hhahahaha");
            channel.write("666666" + index);
        }
        String msg = channel.read();
        System.out.println(msg);
        System.out.println(msg.length());

        msg = channel.read();
        System.out.println(msg);
        System.out.println(msg.length());
        channel.close();
    }
}
