package cn.moyada.screw.loader;

import cn.moyada.screw.enums.ProtocolType;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author xueyikang
 * @create 2018-04-12 18:45
 */
public class NetWordClassLoader extends URLClassLoader {

    private NetWordClassLoader(URL[] urls, ClassLoader parentLoader) {
        super(urls, parentLoader);
    }

    public static NetWordClassLoader newInstance() {
        return newInstance(ClassLoader.getSystemClassLoader());
    }

    public static NetWordClassLoader newInstance(ClassLoader parentLoader) {
        parentLoader = null == parentLoader ? ClassLoader.getSystemClassLoader() : parentLoader;
        return newInstance(Collections.emptyList(), parentLoader);
    }

    public static NetWordClassLoader newInstance(String url) {
        return newInstance(url, ClassLoader.getSystemClassLoader());
    }

    public static NetWordClassLoader newInstance(String url, ClassLoader parentLoader) {
        return newInstance(Collections.singletonList(url), parentLoader);
    }

    public static NetWordClassLoader newInstance(List<String> urls) {
        return newInstance(urls, ClassLoader.getSystemClassLoader());
    }

    public static NetWordClassLoader newInstance(List<String> urlList, ClassLoader parentLoader) {
        URL[] urls = convertUrl(urlList);
        return new NetWordClassLoader(urls, parentLoader);
    }

    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }

    /**
     * 添加jar包至类加载器
     * @param jarUrl
     * @return
     */
    public boolean addNetworkJar(String jarUrl) {
        URL url = convertUrl(jarUrl);
        if(null == url) {
            return false;
        }

        addURL(url);
        return true;
    }

    public boolean addLocalJar(String jarUrl) {
        File file = new File(jarUrl);

        URL url;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        addURL(url);
        return true;
    }

    public boolean addNetworkClass(String pathClass, String classUrl) {
        byte[] bytes = loadClassByNetwork(classUrl);
        if(null == bytes || bytes.length == 0) {
            return false;
        }

        defineClass(pathClass, bytes, 0, bytes.length);
        return true;
    }

    public boolean addLocalClass(String pathClass, String classUrl) {
        byte[] bytes = loadClassByFile(classUrl);
        if(null == bytes || bytes.length == 0) {
            return false;
        }

        defineClass(pathClass, bytes, 0, bytes.length);
        return true;
    }

    public Class<?> getClass(String name) throws ClassNotFoundException {
        // 是否已加载过
        Class<?> clazz = findLoadedClass(name);
        if(null != clazz) {
            return clazz;
        }

        synchronized (this) {
            // 尝试查找
            try {
                clazz = this.findClass(name);
            } catch (Exception e) {
                clazz = null;
            }
        }
        if(null != clazz) {
            resolveClass(clazz);
            return clazz;
        }

        // 交给父加载器
        return super.loadClass(name);
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ResourceBundle.clearCache(this);
    }

    private byte[] loadClassByNetwork(String classUrl) {
        InputStream is;
        try {
            URL url = new URL(classUrl);
            is = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return loadClassData(is);
    }

    private byte[] loadClassByFile(String classFile) {
        InputStream is;
        try {
            is = new FileInputStream(classFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return loadClassData(is);
    }

    private byte[] loadClassData(InputStream is) {
        byte[] buff = new byte[1024];
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len;
            while((len = is.read(buff)) != -1) {
                os.write(buff,0,len);
            }
            buff = os.toByteArray();
            os.close();

            return buff;
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

    private static URL[] convertUrl(List<String> urlList) {
        if(null == urlList || urlList.size() == 0) {
            return new URL[0];
        }

        List<URL> urls = new ArrayList<>(urlList.size());
        URL item;
        int size = 0;
        for (String url : urlList) {
            item = convertUrl(url);
            if(null == item)
                continue;
            urls.add(item);
            size++;
        }
        return urls.toArray(new URL[size]);
    }

    private static URL convertUrl(String url) {
        if(checkUrl(url)) {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return null;
    }

    private static boolean checkUrl(String url) {
        return ProtocolType.checkProtocol(url, ProtocolType.HTTP, ProtocolType.HTTPS);
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String downloadUrl = "https://github.com/moyada/dubbo-faker/raw/mvn-repo/cn/moyada/dubbo-faker-api/1.0.1-SNAPSHOT/dubbo-faker-api-1.0.1-20180402.075241-1.jar";
        String downloadUrl2 = "/Users/xueyikang/Downloads/dubbo-faker-api-1.0.1-20180402.075241-1";
        String classUrl = "/Users/xueyikang/JavaProjects/screw/target/classes/cn/moyada/screw/collection/BiMap.class";

        String classPath = "cn.moyada.screw.collection.BiMap";
        String class2Path = "cn.moyada.dubbo.faker.api.annotation.Exporter";

        NetWordClassLoader classLoader = NetWordClassLoader.newInstance();
        classLoader.addLocalClass(classPath, classUrl);
        Class<?> aClass = classLoader.getClass(classPath);

        classLoader.addNetworkJar(downloadUrl);
//        classLoader.close();

        try {
            aClass = classLoader.getClass(class2Path);
//            aClass = Class.forName(class2Path, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(aClass.getName());
    }
}
