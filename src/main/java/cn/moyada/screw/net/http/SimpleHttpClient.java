package cn.moyada.screw.net.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class SimpleHttpClient extends AbstractHttpClient implements HttpClient {

    public SimpleHttpClient(HttpMode httpMode) {
        super(httpMode);
    }

    @Override
    public HttpResponse get(String url) {
        URI uri;
        if (paramMap.isEmpty()) {
            uri = URI.create(url);
        } else {
            StringBuilder stringBuilder = new StringBuilder(url + "?");
            paramMap.forEach((k, v) -> stringBuilder.append(k).append("=").append(v).append("&"));
            url = stringBuilder.substring(0, stringBuilder.length() - 1);
            uri = URI.create(url);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        headerMap.forEach(builder::header);

        HttpRequest request = builder.GET().build();

        HttpResponse response = new HttpResponse();

        java.net.http.HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            response.setStatusCode(httpResponse.statusCode());
            response.setData(httpResponse.body());
        } catch (IOException e) {
            response.setStatusCode(HttpStatus.TIME_OUT);
            response.setData(e.getMessage());
        } catch (InterruptedException e) {
            response.setStatusCode(HttpStatus.ERROR);
            response.setData(e.getMessage());
        }

        return response;
    }

    @Override
    public HttpResponse post(String url) {
        URI uri = URI.create(url);
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        headerMap.forEach(builder::header);
        builder.POST(HttpRequest.BodyPublishers.noBody());
        return null;
    }

    @Override
    public HttpResponse put(String url) {
        return null;
    }

    @Override
    public HttpResponse delete(String url) {
        return null;
    }
}
