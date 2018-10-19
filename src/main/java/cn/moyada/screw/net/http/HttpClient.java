package cn.moyada.screw.net.http;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface HttpClient {

    HttpMode httpMode = new HttpMode();

    static HttpClient build() {
        return build(httpMode);
    }

    static HttpClient build(HttpMode httpMode) {
        return new SimpleHttpClient(httpMode);
    }

    void header(String key, String value);

    void param(String key, String value);

    HttpResponse get(String url);

    HttpResponse post(String url);

    HttpResponse put(String url);

    HttpResponse delete(String url);
}
