package cn.moyada.screw.net.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author xueyikang
 * @create 2018-03-25 03:11
 */
public abstract class AbstractFile implements Closeable {

    protected static final int BUF_SIZE = 1 << 10;

    protected static final byte NEXT_LINE_FLAG = '\n';

    protected FileChannel channel;

    public void close() {
        try {
            this.channel.close();
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
    }
}
