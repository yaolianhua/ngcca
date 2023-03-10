package io.hotcloud.application.server.chatgpt;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.hotcloud.application.api.chatgpt.ChatCompletionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class ChatGPTFeignClientConfiguration {

    private final ChatGPTProperties chatGPTProperties;

    public ChatGPTFeignClientConfiguration(ChatGPTProperties chatGPTProperties) {
        this.chatGPTProperties = chatGPTProperties;
    }

    @Bean
    public ChatCompletionFeignClient chatCompletionFeignClient(
            ObjectProvider<HttpMessageConverters> httpMessageConvertersObjectProvider,
            ObjectProvider<HttpMessageConverterCustomizer> httpMessageConverterCustomizerObjectProvider
    ) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new SpringEncoder(httpMessageConvertersObjectProvider))
                .decoder(new SpringDecoder(httpMessageConvertersObjectProvider, httpMessageConverterCustomizerObjectProvider))
                .requestInterceptor(new BearerTokenInterceptor())
                .target(ChatCompletionFeignClient.class, chatGPTProperties.getApiUrl());
    }

    @Component
    public class BearerTokenInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header("Authorization", String.format("%s %s", "Bearer", chatGPTProperties.getApiKey()));
        }
    }
}
