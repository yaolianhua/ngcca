package io.hotcloud.server.application.openai;

import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import io.hotcloud.common.model.utils.Log;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Objects;

import static com.theokanning.openai.service.OpenAiService.*;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiConfiguration {

    private final OpenAiProperties openAiProperties;

    public OpenAiConfiguration(OpenAiProperties openAiProperties) {
        this.openAiProperties = openAiProperties;
    }

    @Bean
    public OpenAiService openAiService() {

        Proxy proxy = Proxy.NO_PROXY;
        OpenAiProperties.HttpProxy httpProxy = openAiProperties.getHttpProxy();
        if (Objects.nonNull(httpProxy)) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getHostname(), httpProxy.getPort()));
            Log.info(OpenAiProperties.class.getName(), "【Load openai configuration. use http proxy hostname=" + httpProxy.getHostname() + ", port=" + httpProxy.getPort() + "】");
        }

        OkHttpClient client = defaultClient(openAiProperties.getApiKey(), Duration.ofSeconds(openAiProperties.getHttpClientTimeoutSeconds()))
                .newBuilder()
                .proxy(proxy)
                .build();

        Retrofit retrofit = defaultRetrofit(client, defaultObjectMapper());
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        Log.info(OpenAiProperties.class.getName(), "【Load openai configuration. http-client-timeout-seconds=" + openAiProperties.getHttpClientTimeoutSeconds() + "】");
        return new OpenAiService(api);
    }
}
