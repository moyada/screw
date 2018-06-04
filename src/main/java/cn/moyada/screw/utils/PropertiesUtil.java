package cn.moyada.screw.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author xueyikang
 * @create 2018-05-18 22:29
 */
public class PropertiesUtil {

    public static String get(String key, String propertiesFile) {
        Properties properties = System.getProperties();

        String value = properties.getProperty(key);
        if(null != value) {
            return value;
        }
        
        InputStream input;
        try {
            input = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties.getProperty(key);
    }

    public static String get(String key) {
        return System.getProperty(key);
    }
}
