package cn.moyada.screw.utils;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xueyikang
 * @create 2018-03-20 17:13
 */
public final class HttpUtil {

//    private static final HttpClient instance = new SSLClient();
    private static final HttpClient instance = HttpClientBuilder.create().build();

    public static String post(String url, Map<String, String> params) {
//        url = combineParam(url, params);
        List<NameValuePair> pairs = new ArrayList<>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }


        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        return request(httpPost);
    }

    public static String get(String url) {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> params) {
        url = combineParam(url, params);

        HttpGet httpGet = new HttpGet(url);
        return request(httpGet);
    }

    private static String request(HttpUriRequest request) {
        try {
            HttpResponse response = instance.execute(request);
            return getDate(response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String combineParam(String url, Map<String, String> params) {
        if(null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder newUrl = new StringBuilder(url);
        newUrl.append("?");
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                newUrl.append(entry.getKey());
                newUrl.append("=");
                newUrl.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                newUrl.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return newUrl.toString();
    }

    private static String getDate(HttpResponse response) {
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
