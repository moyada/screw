package cn.moyada.screw.utils;

import cn.moyada.screw.loader.NetWordClassLoader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

/**
 * 系统工具
 * @author xueyikang
 * @create 2018-04-13 10:30
 */
public class RuntimeUtil {

    private static final HttpClient CLIENT = HttpClientBuilder.create().build();

    /**
     * 获取FullGC次数
     * @return
     */
    public static long getGCCount() {
        long gcCount = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcCount += garbageCollectorMXBean.getCollectionCount();
        }
        return gcCount;
    }

    /**
     * 添加jar包至系统默认类加载器
     * @param url
     * @return
     */
    public static boolean addJarToSystemClassLoader(String url) {
        if(!checkUrl(url)) {
            return false;
        }

        try {
            return addJarToClassLoader(new URL(url), (URLClassLoader) ClassLoader.getSystemClassLoader());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 增加jar包至指定的类加载器
     * @param url
     * @param classLoader
     * @return
     */
    private static boolean addJarToClassLoader(URL url, URLClassLoader classLoader) {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            add.setAccessible(true);
            add.invoke(classLoader, url);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static byte[] getClassData(String path) {
        InputStream is = null;
        try {
            URL url = new URL(path);
            byte[] buff = new byte[1024*4];
            int len = -1;
            is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len = is.read(buff)) != -1) {
                baos.write(buff,0,len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 判断URL是否有效
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        HttpResponse response;
        try {
            response = CLIENT.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return response.getStatusLine().getStatusCode() == 200;
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String downloadUrl = "https://repo.souche-inc.com/repository/snapshots/com/souche/car-model-api/1.2.1-SNAPSHOT/car-model-api-1.2.1-20180411.032511-10.jar";
        String classUrl = "https://raw.githubusercontent.com/moyada/dubbo-faker/master/dubbo-faker-api/src/main/java/cn/moyada/dubbo/faker/api/annotation/Exporter.java";

        String classPath = "com.souche.car.model.api.model.BrandService";
        String class2Path = "cn.moyada.dubbo.faker.api.annotation.Exporter";

        NetWordClassLoader netWordClassLoader = NetWordClassLoader.newInstance(Collections.emptyList());
        Class<?> aClass = netWordClassLoader.loadNetWordClass(class2Path, classUrl);

        addJarToSystemClassLoader(downloadUrl);
//        String class2Path = "cn.moyada.screw.model.BaseDO";
        Class c;

        c=Class.forName(classPath);
        System.out.println(c.isInterface());
    }
}
