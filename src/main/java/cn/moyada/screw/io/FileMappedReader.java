package cn.moyada.screw.io;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * @author xueyikang
 * @create 2018-03-24 22:25
 */
public class FileMappedReader extends AbstractFileReader {

    private char[] data;

    private CharBuffer buffer;

    public FileMappedReader(String fileName) throws IOException {
        Path path = Paths.get(fileName);

        this.channel = (FileChannel) Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ));
        MappedByteBuffer byteBuffer = this.channel.map(FileChannel.MapMode.READ_ONLY, 0, this.channel.size());
        if (byteBuffer == null) {
            throw new IOException("mapped buffer error.");
        }
        this.buffer = StandardCharsets.UTF_8.decode(byteBuffer);
        this.data = new char[BUF_SIZE << 1];
        this.readPos = 0;
        this.limit = this.buffer.limit();
    }

    @Override
    public boolean hasNext() {
        return remain();
    }

    @Override
    public String nextLine() {
        // 如果buffer还有数据
        char c;
        int index = 0;
        for (; remain(); index++, readPos++) {
            c = buffer.get(readPos);
            if(c == NEXT_LINE_FLAG) {
                readPos++;
                return new String(data, 0, index);
            }
            data[index] = c;
        }

        super.close();
        return new String(data, 0, index);
    }

    public static void main(String[] args) throws IOException {
        FileMappedReader fileChannelReader = new FileMappedReader("/Users/xueyikang/screw/src/main/java/cn/moyada/screw/io/FileMappedReader.java");
        while (fileChannelReader.hasNext()) {
            System.out.println(fileChannelReader.nextLine());
        }
    }
}
