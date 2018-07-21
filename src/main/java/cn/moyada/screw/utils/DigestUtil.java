package cn.moyada.screw.utils;

import cn.moyada.screw.pool.BeanPool;
import cn.moyada.screw.pool.BeanPoolFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;

public class DigestUtil {

    private static BeanPool<MessageDigest> MD5_DIGEST =
            BeanPoolFactory.newConcurrentPool(1, DigestUtil::newMD5Instance);

    private static MessageDigest newMD5Instance() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static MessageDigest getMd5Digest() {
        return MD5_DIGEST.allocate();
    }

    private static void resetMd5Digest(MessageDigest messageDigest) {
        messageDigest.reset();
        MD5_DIGEST.recycle(messageDigest);
    }

    private static String byteToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toUpperCase();
    }

    public static String fileToMD5(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        FileChannel fileChannel = (FileChannel) Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byte[] bytes;
        int len;
        MessageDigest messageDigest = getMd5Digest();
        while ((len = fileChannel.read(byteBuffer)) > 0) {
            bytes = byteBuffer.array();
            messageDigest.update(bytes, 0, len);
        }

        byte[] digest = messageDigest.digest();
        resetMd5Digest(messageDigest);
        return byteToHex(digest);
    }

    public static String getMD5(String input) {
        MessageDigest messageDigest = getMd5Digest();
        byte[] digest = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
        resetMd5Digest(messageDigest);
        return byteToHex(digest);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(fileToMD5("/Users/xueyikang/JavaProjects/screw/src/main/resources/redis.yaml"));
        System.out.println(getMD5("dsad4354354354354355as"));
    }
}
