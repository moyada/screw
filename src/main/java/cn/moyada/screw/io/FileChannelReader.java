package cn.moyada.screw.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xueyikang
 * @create 2018-03-24 22:25
 */
public class FileChannelReader extends AbstractFileReader {
    private boolean hasNext;
    private byte[] data;
    private ByteBuffer buffer;

    public FileChannelReader(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        this.channel = FileChannel.open(path);
        this.buffer = ByteBuffer.allocateDirect(BUF_SIZE);

        this.data = new byte[BUF_SIZE << 1];
        this.hasNext = true;
        this.readPos = 0;
    }

    public boolean hasNext() {
        return this.hasNext || remain();
    }

    public String nextLine() {
        // 如果buffer还有数据
        byte b;
        int index = 0;
        if(remain()) {
            for (; remain(); index++, readPos++) {
                b = buffer.get(readPos);
                if(b == NEXT_LINE_FLAG) {
                    readPos++;
                    return new String(data, 0, index);
                }
                data[index] = b;
            }
        }

        if(!hasNext) {
            if(index > 0) {
                return new String(data, 0, index);
            }
            throw cn.moyada.screw.exception.IOException.CHANNEL_ALREADY_CLOSED;
        }

        readNextBuffer();

        if(limit < BUF_SIZE) {
            close();
        }

        for (readPos = 0; remain(); index++, readPos++) {
            b = buffer.get(readPos);
            if(b == NEXT_LINE_FLAG) {
                readPos++;
                return new String(data, 0, index);
            }
            data[index] = b;
        }

        // has bug when the size bigger than 1024 of one line.
        return new String(data, 0, index);
    }

    private void readNextBuffer() {
        try {
            limit = channel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        buffer.flip();
    }

    public void close() {
        super.close();
        this.hasNext = false;
    }

    public static void main(String[] args) throws IOException {
        FileChannelReader fileChannelReader = new FileChannelReader("/Users/xueyikang/screw/src/main/java/cn/moyada/screw/common/FileReader.java");
        while (fileChannelReader.hasNext()) {
            System.out.println(fileChannelReader.nextLine());
        }
        System.out.println(fileChannelReader.channel.isOpen());
    }
}
