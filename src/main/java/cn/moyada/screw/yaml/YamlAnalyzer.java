package cn.moyada.screw.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-01-15 14:24
 */
public class YamlAnalyzer {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> analyze(String yamlPath) {
        InputStream input = YamlAnalyzer.class.getResourceAsStream(yamlPath);
        if(null == input) {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Yaml yaml = new Yaml();
        Object result = yaml.load(input);
        return (Map<String, Object>) result;
    }

    public static void main(String[] args) {
        Map<String, Object> yamlResult = YamlAnalyzer.analyze("/redis.yaml");
        System.out.println(yamlResult);
    }
}
