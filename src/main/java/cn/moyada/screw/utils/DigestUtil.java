package cn.moyada.screw.utils;

import cn.moyada.screw.pool.ObjectPool;
import cn.moyada.screw.pool.ObjectPoolFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;

//import javax.xml.bind.DatatypeConverter;

public class DigestUtil {

    private static ObjectPool<MessageDigest> MD5_DIGEST =
            ObjectPoolFactory.newConcurrentPool(1, DigestUtil::newMD5Instance);

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

    private static String byteToUpperHex(byte[] bytes) {
        int length = bytes.length;
        StringBuilder str = new StringBuilder(length * 2);
        String item;
        byte b;
        for (int index = 0; index < length; index++) {
            b = bytes[index];
            if (b < 0) {
                item = Integer.toHexString(b & 0xFF);
            } else {
                item = Integer.toHexString(b);
            }

            if (item.length() == 1) {
                str.append('0');
            }
            str.append(item);
        }
        return str.toString().toUpperCase();
    }

    public static byte[] getMD5bytes(byte[] input, byte[]... salts) {
        MessageDigest messageDigest = getMd5Digest();
        messageDigest.update(input);
        for (byte[] salt : salts) {
            messageDigest.update(salt);
        }

        byte[] digest = messageDigest.digest();
        resetMd5Digest (messageDigest);
        return digest;
    }

    public static String getMD5(byte[] input) {
        MessageDigest messageDigest = getMd5Digest();
        byte[] digest = messageDigest.digest(input);
        resetMd5Digest(messageDigest);
        return byteToUpperHex(digest);
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
        return byteToUpperHex(digest);
    }

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        System.out.println(fileToMD5("/Users/xueyikang/JavaProjects/screw/src/main/resources/redis.yaml"));
        System.out.println(getMD5("dsad4354354354354355as".getBytes()));
        System.out.println(System.nanoTime() - start);
    }
}
