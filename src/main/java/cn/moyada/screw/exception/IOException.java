package cn.moyada.screw.exception;

import java.io.FileNotFoundException;
import java.nio.channels.ClosedChannelException;

/**
 * @author xueyikang
 * @create 2018-03-25 20:35
 */
public class IOException extends RuntimeException {

    public static final IOException CHANNEL_ALREADY_CLOSED = new IOException(new ClosedChannelException());
    public static final IOException IO_ERROR = new IOException(new java.io.IOException());
    public static final IOException NULL_ERROR = new IOException(new NullPointerException("io can not be null."));
    public static final IOException FILE_NOT_FOUNT = new IOException(new FileNotFoundException("current file can not be fount."));

    private static final long serialVersionUID = -8958937454226353465L;

    public IOException(String msg) {
        super(new java.io.IOException(msg));
    }

    public IOException(Exception e) {
        super(e);
    }
}
