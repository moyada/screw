package cn.moyada.screw.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * @author xueyikang
 * @create 2018-03-25 03:10
 */
public class FileChannelWriter extends AbstractFileWriter {

    public FileChannelWriter(String fileName) throws IOException {
        createIfNotExists(fileName);

        this.channel = new RandomAccessFile(fileName, "rw").getChannel();
        this.buffer = ByteBuffer.allocateDirect(BUF_SIZE);
    }

    public void write(String data) {
        buffer.put(data.getBytes());
        writeAndClear();
    }

    public void nextLine() {
        buffer.put(NEXT_LINE_FLAG);
        writeAndClear();
    }

    private void writeAndClear() {
        buffer.flip();
        try {
            channel.write(buffer);
        } catch (IOException e) {
            super.close();
            throw new cn.moyada.screw.exception.IOException(e);
        }
        this.buffer.clear();
    }

    public static void main(String[] args) throws IOException {
        FileChannelWriter write = new FileChannelWriter("test.txt");
        write.write("xiexie");
        write.nextLine();
        write.write("haha");
        write.write("666");
        write.nextLine();
        write.close();
    }
}
