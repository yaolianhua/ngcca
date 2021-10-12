package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class EnvFrom {

    private String prefix;
    private ConfigMapEnvSource configMapRef;
    private SecretEnvSource secretRef;

    @Data
    @Builder
    public static class ConfigMapEnvSource{
        private String name;
        private Boolean optional;
    }
    @Data
    @Builder
    public static class SecretEnvSource{
        private String name;
        private Boolean optional;
    }
}
