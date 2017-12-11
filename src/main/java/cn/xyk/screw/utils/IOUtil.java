package cn.xyk.screw.utils;

import java.io.BufferedReader;
import java.io.IOException;
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
public class IOUtil {

    public static Map<String, String> read2Map(String fileName, FileOpt<Stream<String>, Map<String, String>, IOException> block) throws IOException {
        return read2Map(fileName, block, StandardCharsets.UTF_8);
    }

    public static Map<String, String> read2Map(String fileName, FileOpt<Stream<String>, Map<String, String>, IOException> block, Charset charSet) throws IOException {
        Map<String, String> result;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charSet)){
            result = block.apply(reader.lines());
        }
        return result;
    }

    public static List<String> read2List(String fileName, FileOpt<Stream<String>, List<String>, IOException> block) throws IOException {
        return read2List(fileName, block, StandardCharsets.UTF_8);
    }

    public static List<String> read2List(String fileName, FileOpt<Stream<String>, List<String>, IOException> block, Charset charSet) throws IOException {
        List<String> result;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName), charSet)){
            result = block.apply(reader.lines());
        }
        return result;
    }

    @FunctionalInterface
    public interface FileOpt<T, R, X extends Throwable> {
        R apply(T instance) throws X;
    }
}
