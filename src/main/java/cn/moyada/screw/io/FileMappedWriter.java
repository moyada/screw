package cn.moyada.screw.io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * @author xueyikang
 * @create 2018-03-25 03:10
 */
public class FileMappedWriter extends AbstractFileWriter {

    private long pos;

    public FileMappedWriter(String fileName) throws IOException {
        Path path = createIfNotExists(fileName);

        this.channel = (FileChannel) Files.newByteChannel(path, EnumSet.of(
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING));
        this.pos = 0;
    }

    private void initBuffer(int size) {
        try {
            this.buffer = this.channel.map(FileChannel.MapMode.READ_WRITE, pos, size);
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
        pos = pos + size;
    }

    @Override
    public void write(String data) {
        initBuffer(data.length());
        this.buffer.put(data.getBytes());
    }

    @Override
    public void nextLine() {
        initBuffer(1);
        this.buffer.put(NEXT_LINE_FLAG);
    }

    public static void main(String[] args) throws IOException {
        FileMappedWriter write = new FileMappedWriter("test.txt");
        write.write("xiexie");
        write.nextLine();
        write.write("haha");
        write.write("666");
        write.nextLine();
        write.close();
    }
}
