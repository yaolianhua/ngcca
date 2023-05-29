package io.hotcloud.service.module.registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.exception.PlatformException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "system.registry-image")
@Configuration(proxyBeanMethods = false)
@Properties(prefix = CONFIG_PREFIX + "system.registry-image")
@Data
public class SystemRegistryImageProperties {

    private String minio;

    private String mysql;

    private String mongodb;

    private String redis;

    private String redisinsight;

    private String rabbitmq;

    private String busybox;

    private String kaniko;

    private String git;

    private String alpine;

    private String java8;

    private String java11;

    private String java17;

    private String maven3808;

    private String maven3811;

    private String maven3817;

    public Map<String, String> getPropertyPair() {

        Map<String, String> property = new HashMap<>();

        Field[] declaredFields = SystemRegistryImageProperties.class.getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                Object o = field.get(this);
                if (o == null || String.valueOf(o).isBlank()) {
                    continue;
                }

                JsonProperty jsonPropertyAnno = field.getAnnotation(JsonProperty.class);
                if (jsonPropertyAnno != null) {
                    property.put(jsonPropertyAnno.value(), o.toString());
                    continue;
                }
                property.put(field.getName(), o.toString());
            } catch (IllegalAccessException e) {
                throw new PlatformException(e.getMessage(), e);
            }
        }


        return property;
    }

    public String getPropertyValue(String propertyName) {
        return this.getPropertyPair().get(propertyName);
    }

    /**
     * @param propertyName rabbitmq
     * @return 3.9-management
     */
    public String getTag(String propertyName) {
        String propertyValue = this.getPropertyPair().get(propertyName);
        int pos = propertyValue.indexOf(":");
        return propertyValue.substring(pos + 1);
    }

}
