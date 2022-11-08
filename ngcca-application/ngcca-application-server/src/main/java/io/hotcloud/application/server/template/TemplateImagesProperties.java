package io.hotcloud.application.server.template;

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

@ConfigurationProperties(prefix = "template.image.name")
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:template-images.properties")
@Properties(prefix = "template.image.name")
@Data
public class TemplateImagesProperties {

    /**
     * minio image name e.g. namespace/minio:latest
     */
    private String minio;
    /**
     * mysql image name e.g. namespace/mysql:8.0
     */
    private String mysql;
    /**
     * mongodb image name e.g. namespace/mongo:5.0
     */
    private String mongodb;
    /**
     * redis image name e.g. namespace/redis:7.0
     */
    private String redis;
    /**
     * redisinsight image name e.g. namespace/redisinsight:latest
     */
    private String redisinsight;
    /**
     * rabbitmq image name e.g. namespace/rabbitmq:3.9-management
     */
    private String rabbitmq;

    public Map<String, String> getRepos() {
        Field[] declaredFields = TemplateImagesProperties.class.getDeclaredFields();
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
