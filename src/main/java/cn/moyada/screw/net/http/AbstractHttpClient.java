package cn.moyada.screw.net.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xueyikang
 * @since 1.0
 **/
public abstract class AbstractHttpClient implements HttpClient {

    protected final java.net.http.HttpClient httpClient;

    protected final Map<String, String> paramMap;

    protected final Map<String, String> headerMap;

    public AbstractHttpClient(HttpMode httpMode) {
        java.net.http.HttpClient.Builder builder = java.net.http.HttpClient.newBuilder();

        this.paramMap = new HashMap<>();
        this.headerMap = new HashMap<>();

        if (Objects.isNull(httpMode)) {
            this.httpClient = builder.build();
            return;
        }

        if (Objects.nonNull(httpMode.getRedirect())) {
            builder.followRedirects(httpMode.getRedirect());
        }
        if (Objects.nonNull(httpMode.getVersion())) {
            builder.version(httpMode.getVersion());
        }
        if (Objects.nonNull(httpMode.getProxy())) {
            builder.proxy(httpMode.getProxy());
        }
        if (Objects.nonNull(httpMode.getAuthenticator())) {
            builder.authenticator(httpMode.getAuthenticator());
        }
        if (Objects.nonNull(httpMode.getTimeout())) {
            builder.connectTimeout(httpMode.getTimeout());
        }

        this.httpClient = builder.build();
    }

    @Override
    public void header(String key, String value) {
        headerMap.put(key, value);
    }

    @Override
    public void param(String key, String value) {
        paramMap.put(key, value);
    }
}
