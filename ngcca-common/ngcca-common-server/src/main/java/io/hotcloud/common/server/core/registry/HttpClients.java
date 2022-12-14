package io.hotcloud.common.server.core.registry;

import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class HttpClients {

    @SneakyThrows
    public static HttpClient acceptsUntrustedCertsHttpClient() {
        HttpClientBuilder httpClientBuilder = org.apache.http.impl.client.HttpClients.custom();

        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(((x509Certificates, authType) -> true)).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
        httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());

        return httpClientBuilder.build();
    }
}
