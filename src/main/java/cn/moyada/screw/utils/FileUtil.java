package cn.moyada.screw.utils;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.EnumSet;

/**
 * @author xueyikang
 * @create 2018-03-25 20:55
 */
public class FileUtil {
    
    public static boolean createFile(String filePath) {
        return createFile(Paths.get(filePath));
    }

    private static boolean createFile(Path filePath) {
        if(Files.exists(filePath)) {
            return false;
        }

        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void clearOrCreateFile(String filePath) {
        Path path = Paths.get(filePath);
        if(!createFile(path)) {
            try {
                Files.write(path, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                throw new cn.moyada.screw.exception.IOException(e);
            }
        }
    }

    public static void copy(String sourceFile, String targetFile) throws IOException {
        Path sourcePath = Paths.get(sourceFile);
        if(!Files.exists(sourcePath)) {
            throw cn.moyada.screw.exception.IOException.FILE_NOT_FOUNT;
        }
        Path copied = Paths.get(targetFile);
        if(!Files.exists(copied)) {
            Files.createFile(copied);
        }

        try(FileChannel open = FileChannel.open(sourcePath);
            FileChannel target = FileChannel.open(copied))
        {
            open.transferTo(0, open.size(), target);
        }
//        catch (IllegalArgumentException | NonReadableChannelException |
//                NonWritableChannelException | ClosedChannelException e)
//        {
//            // ignore
//            return false;
//        }
//        return true;
    }

    public static String read(String filePath) {
        Path path = Paths.get(filePath);
        if(Files.notExists(path)) {
            throw cn.moyada.screw.exception.IOException.FILE_NOT_FOUNT;
        }

        MappedByteBuffer mappedByteBuffer;
        try {
            FileChannel channel = (FileChannel) Files.newByteChannel(
                    path, EnumSet.of(StandardOpenOption.READ));
            mappedByteBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }

        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(mappedByteBuffer);
        return new String(charBuffer.array());
    }

    public static void append(String filePath, String data) {
        Path path = Paths.get(filePath);
        createFile(path);

        try {
            Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
    }

    public static void appendByMapped(String fileName, String data) {
        try {
            FileChannel channel = (FileChannel) Files
                    .newByteChannel(Paths.get(fileName), EnumSet.of(
                            StandardOpenOption.READ,
                            StandardOpenOption.WRITE));
            channel.map(FileChannel.MapMode.READ_WRITE, channel.size(), data.length())
                    .put(StandardCharsets.UTF_8.encode(data));
        } catch (IOException e) {
            throw new cn.moyada.screw.exception.IOException(e);
        }
    }
}
