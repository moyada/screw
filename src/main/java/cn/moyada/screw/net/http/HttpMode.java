package cn.moyada.screw.net.http;

import java.net.Authenticator;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * @author xueyikang
 * @since 1.0
 **/
public class HttpMode {

    private HttpClient.Redirect redirect = HttpClient.Redirect.NORMAL;

    private HttpClient.Version version = HttpClient.Version.HTTP_1_1;

    private ProxySelector proxy;

    private Authenticator authenticator = Authenticator.getDefault();

    private Duration timeout = Duration.ofSeconds(30L);

    public HttpClient.Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(HttpClient.Redirect redirect) {
        this.redirect = redirect;
    }

    public HttpClient.Version getVersion() {
        return version;
    }

    public void setVersion(HttpClient.Version version) {
        this.version = version;
    }

    public ProxySelector getProxy() {
        return proxy;
    }

    public void setProxy(ProxySelector proxy) {
        this.proxy = proxy;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public static void main(String[] args) {
        HttpMode httpMode = new HttpMode();
        cn.moyada.screw.net.http.HttpClient httpClient = cn.moyada.screw.net.http.HttpClient.build(httpMode);
    }
}
