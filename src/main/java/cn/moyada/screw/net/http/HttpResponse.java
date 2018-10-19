package cn.moyada.screw.net.http;

public class HttpResponse {

    private int statusCode;

    private String data;

    public HttpResponse() {
    }

    public HttpResponse(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
