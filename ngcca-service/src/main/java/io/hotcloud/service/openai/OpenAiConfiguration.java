package io.hotcloud.service.openai;

import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.Objects;

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
        }

        OkHttpClient client = OpenAiService.defaultClient(openAiProperties.getApiKey(), Duration.ofSeconds(openAiProperties.getHttpClientTimeoutSeconds())).newBuilder()
                .proxy(proxy)
                .build();

        Retrofit retrofit = OpenAiService.defaultRetrofit(client, OpenAiService.defaultObjectMapper());
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        Log.info(this, openAiProperties, Event.START, "load openAi properties");
        return new OpenAiService(api);
    }
}
