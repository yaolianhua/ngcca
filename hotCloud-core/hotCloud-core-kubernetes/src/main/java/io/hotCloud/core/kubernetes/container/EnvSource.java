package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class EnvSource {

    private ConfigMapKeySelector configMapKeyRef;
    private SecretKeySelector secretKeyRef;

    @Data
    @Builder
    public static class ConfigMapKeySelector{
        private String key;
        private String name;
        private Boolean optional;
    }
    @Data
    @Builder
    public static class SecretKeySelector{
        private String key;
        private String name;
        private Boolean optional;
    }
}
