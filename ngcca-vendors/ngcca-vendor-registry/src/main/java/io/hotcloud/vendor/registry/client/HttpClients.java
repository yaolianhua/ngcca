package io.hotcloud.vendor.registry.client;

import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

public class HttpClients {
    private HttpClients() {
    }

    @SneakyThrows
    public static HttpClient acceptsUntrustedCertsHttpClient() {


        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(((x509Certificates, authType) -> true)).build();
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        PoolingHttpClientConnectionManager httpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder
                .create().setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        HttpClientBuilder httpClientBuilder = org.apache.hc.client5.http.impl.classic.HttpClients
                        .custom()
                        .setConnectionManager(httpClientConnectionManager)
                        .setDefaultRequestConfig(RequestConfig.DEFAULT);

        return httpClientBuilder.build();
    }
}
