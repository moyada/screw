package cn.moyada.screw.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 文件读取流
 * Created by xueyikang on 2017/12/12.
 */
public interface IOUtil {

    public static String read(InputStream in) {
        if(null == in) {
            throw cn.moyada.screw.exception.IOException.NULL_ERROR;
        }
        BufferedReader reader = new BufferedReader (new InputStreamReader(in));

        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            throw cn.moyada.screw.exception.IOException.IO_ERROR;
        }
        return sb.toString();
    }

    public static <K, V> Map<K, V> readToMap(String fileName, FileOpt<Stream<String>, Map<K, V>, IOException> block) throws IOException {
        return readToMap(fileName, block, StandardCharsets.UTF_8);
    }

    public static <K, V> Map<K, V>  readToMap(String fileName, FileOpt<Stream<String>, Map<K, V>, IOException> block, Charset charSet) throws IOException {
        Map<K, V> result;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charSet)){
            result = block.apply(reader.lines());
        }
        return result;
    }

    public static <T> List<T> readToList(String fileName, FileOpt<Stream<String>, List<T>, IOException> block) throws IOException {
        return readToList(fileName, block, StandardCharsets.UTF_8);
    }

    public static <T> List<T> readToList(String fileName, FileOpt<Stream<String>, List<T>, IOException> block, Charset charSet) throws IOException {
        List<T> result;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charSet)){
            result = block.apply(reader.lines());
        }
        return result;
    }

    public static <T> T readToObject(String fileName, FileOpt<Stream<String>, T, IOException> block) throws IOException {
        return readToObject(fileName, block, StandardCharsets.UTF_8);
    }

    public static <T> T readToObject(String fileName, FileOpt<Stream<String>, T, IOException> block, Charset charSet) throws IOException {
        T result;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charSet)){
            result = block.apply(reader.lines());
        }
        return result;
    }

    @FunctionalInterface
    public interface FileOpt<T extends Stream, R, X extends Throwable> {
        R apply(T stream) throws X;
    }

    public static void main(String[] args) throws IOException {
        String realPath = CommonUtil.getRealPath("redis.yaml");
        int strings = readToObject(realPath, stream ->
                stream.mapToInt(String::length).sum());
        System.out.println(strings);
    }
}
