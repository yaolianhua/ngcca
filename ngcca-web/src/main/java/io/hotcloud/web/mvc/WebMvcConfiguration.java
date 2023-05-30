package io.hotcloud.web.mvc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final CookieUserArgumentResolver cookieUserArgumentResolver;

    public WebMvcConfiguration(CookieUserArgumentResolver cookieUserArgumentResolver) {
        this.cookieUserArgumentResolver = cookieUserArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(cookieUserArgumentResolver);
    }
}
