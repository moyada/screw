package cn.moyada.screw.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author xueyikang
 * @create 2018-03-25 23:55
 */
public class AsyncFileWriter extends AbstractFileWriter {

    private static final int BUF_SIZE = 1024;

    private final LongAdder longAdder;

    private int posIndex;
    private long limit;
    private List<String> data;
    private ByteBuffer buffer;
    private AsynchronousFileChannel channel;

    private FileCompletionHandler completionHandler;

    public AsyncFileWriter(String fileName) {
        Path path = Paths.get(fileName);

        try {
            this.channel = AsynchronousFileChannel.open(
                    path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            this.limit = channel.size();
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
        this.buffer = ByteBuffer.allocate(BUF_SIZE);
        this.data = new ArrayList<>();
        this.posIndex = 0;
        this.longAdder = new LongAdder();

        this.completionHandler = new FileCompletionHandler();
    }

    @Override
    public void write(String data) {
        buffer.put(data.getBytes());
        buffer.flip();
        channel.write(buffer, 0, buffer, completionHandler);
    }

    @Override
    public void nextLine() {
        buffer.put((byte) '\n');
        buffer.flip();
        channel.write(buffer, 0, buffer, completionHandler);
    }

    class FileCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            byte b;
            byte[] bytes = new byte[result];
            for (int index = 0; index < result; index++) {
                b = buffer.get(index);
                if(b == '\n') {
                    data.add(new String(bytes, 0, index));
                }
                bytes[index] = b;
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            exc.printStackTrace();
        }
    }
}
