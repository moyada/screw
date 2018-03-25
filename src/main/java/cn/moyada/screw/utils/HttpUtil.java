package cn.moyada.screw.utils;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-03-20 17:13
 */
public class HttpUtil {

    private static final HttpClient instance = HttpClientBuilder.create().build();

    public static String post(String url, Map<String, String> params) {
        if(!params.isEmpty()) {
            StringBuilder newUrl = new StringBuilder(url);
            newUrl.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                newUrl.append(entry.getKey());
                newUrl.append("=");
                newUrl.append(entry.getValue());
                newUrl.append("&");
            }
            url = newUrl.toString();
        }

        HttpPost httpPost = new HttpPost(url);
//        List<NameValuePair> pairs = new ArrayList<>(params.size());
        String data;
        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
            HttpResponse response = instance.execute(httpPost);
            data = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }
}
