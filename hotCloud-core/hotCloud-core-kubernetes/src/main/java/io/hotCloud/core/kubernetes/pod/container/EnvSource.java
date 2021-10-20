package io.hotCloud.core.kubernetes.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class EnvSource {

    private ConfigMapKeySelector configMapKeyRef;
    private SecretKeySelector secretKeyRef;

    @Data
    public static class ConfigMapKeySelector{
        private String key;
        private String name;
        private Boolean optional;
    }
    @Data
    public static class SecretKeySelector{
        private String key;
        private String name;
        private Boolean optional;
    }
}
