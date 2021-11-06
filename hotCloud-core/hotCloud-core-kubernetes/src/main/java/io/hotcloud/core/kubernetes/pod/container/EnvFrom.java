package io.hotcloud.core.kubernetes.pod.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class EnvFrom {

    private String prefix;
    private ConfigMapEnvSource configMapRef;
    private SecretEnvSource secretRef;

    @Data
    public static class ConfigMapEnvSource {
        private String name;
        private Boolean optional;
    }

    @Data
    public static class SecretEnvSource {
        private String name;
        private Boolean optional;
    }
}
