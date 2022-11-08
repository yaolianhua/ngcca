package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.exception.HotCloudException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "buildpack.image.name")
@PropertySource("classpath:buildpack-images.properties")
@Data
@Properties(prefix = "buildpack.image.name")
public class BuildPackImagesProperties {

    /**
     * kaniko image name e.g. namespace/kaniko:latest
     */
    private String kaniko;
    /**
     * git image name e.g. namespace/git:latest
     */
    private String git;
    /**
     * alpine image name e.g. namespace/alpine:latest
     */
    private String alpine;
    /**
     * java11 image name e.g. namespace/java11-runtime:latest
     */
    private String java11;
    /**
     * maven image name e.g. library/maven:3.8-openjdk-11-slim
     */
    private String maven;

    public Map<String, String> getRepos() {
        Field[] declaredFields = BuildPackImagesProperties.class.getDeclaredFields();
        Map<String, String> args = new HashMap<>(32);
        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                Object o = field.get(this);
                if (o == null) {
                    continue;
                }
                if (o instanceof String && !StringUtils.hasText(((String) o))) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    args.put(jsonProperty.value(), String.valueOf(o));
                    continue;
                }
                args.put(field.getName(), String.valueOf(o));
            } catch (IllegalAccessException e) {
                throw new HotCloudException(e.getMessage(), e);
            }
        }

        return args;
    }
}
