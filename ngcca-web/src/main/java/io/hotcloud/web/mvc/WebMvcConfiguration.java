package io.hotcloud.web.mvc;

import io.hotcloud.web.WebServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final CookieUserArgumentResolver cookieUserArgumentResolver;
    private final WebServerProperties webServerProperties;

    public WebMvcConfiguration(CookieUserArgumentResolver cookieUserArgumentResolver,
                               WebServerProperties webServerProperties) {
        this.cookieUserArgumentResolver = cookieUserArgumentResolver;
        this.webServerProperties = webServerProperties;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(cookieUserArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedOrigins(webServerProperties.getEndpoint());
    }
}
