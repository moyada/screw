package cn.moyada.screw.net.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author xueyikang
 * @create 2018-03-25 03:10
 */
public abstract class AbstractFileWriter extends AbstractFile {

    protected ByteBuffer buffer;

    protected Path createIfNotExists(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if(Files.notExists(path)) {
            Files.createFile(path);
        }
        return path;
    }

    public abstract void write(String data) throws IOException;

    public abstract void nextLine() throws IOException;
}
