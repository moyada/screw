package cn.moyada.screw.utils;


import cn.moyada.screw.enums.HttpMethod;
import cn.moyada.screw.net.http.HttpHeader;
import cn.moyada.screw.pool.ObjectPool;
import cn.moyada.screw.pool.ObjectPoolFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Http2Util {

    private static ObjectPool<HttpClient> client = ObjectPoolFactory.newConcurrentPool(1, HttpClient::newHttpClient);

    public static cn.moyada.screw.net.http.HttpResponse execute(String url, HttpMethod method, List<HttpHeader> headers, Map<String, String> param) throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = client.allocate();
        HttpRequest httpRequest = buildRequest(url, method, headers, param);
        HttpResponse<String> response = httpClient.send(httpRequest, new ResponseBodyHandler());
        return new cn.moyada.screw.net.http.HttpResponse(response.statusCode(), response.body());
    }

    private static HttpRequest buildRequest(String requestUrl, HttpMethod method,
                                            List<HttpHeader> headers, Map<String, String> param)
            throws URISyntaxException {

        URI url = new URI(requestUrl);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpRequest.BodyPublisher bodyPublisher;
        if (CollectionUtil.isEmpty(param)) {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }
        else {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(param.toString(), StandardCharsets.UTF_8);
        }

        requestBuilder = requestBuilder.uri(url).method(method.name(), bodyPublisher);
        for (HttpHeader header : headers) {
            requestBuilder = requestBuilder.header(header.getName(), header.getValue());
        }

        return requestBuilder.build();
    }

    static class ResponseBodyHandler implements HttpResponse.BodyHandler<String> {

        @Override
        public HttpResponse.BodySubscriber<String> apply(HttpResponse.ResponseInfo responseInfo) {
            return HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        cn.moyada.screw.net.http.HttpResponse response = execute("https://www.baidu.com", HttpMethod.GET, null, null);
        System.out.println(response);
    }
}
