package io.hotcloud.core.kubernetes.pod.container;

import io.kubernetes.client.openapi.models.V1ConfigMapEnvSource;
import io.kubernetes.client.openapi.models.V1EnvFromSource;
import io.kubernetes.client.openapi.models.V1SecretEnvSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class EnvFromSourceBuilder {
    private EnvFromSourceBuilder() {
    }

    public static V1EnvFromSource build(EnvFrom envFrom) {

        V1EnvFromSource v1EnvFromSource = new V1EnvFromSource();
        v1EnvFromSource.setPrefix(envFrom.getPrefix());

        EnvFrom.ConfigMapEnvSource configMapRef = envFrom.getConfigMapRef();
        if (Objects.nonNull(configMapRef)) {
            V1ConfigMapEnvSource v1ConfigMapEnvSource = new V1ConfigMapEnvSource();
            v1ConfigMapEnvSource.setName(configMapRef.getName());
            v1ConfigMapEnvSource.setOptional(configMapRef.getOptional());
            v1EnvFromSource.setConfigMapRef(v1ConfigMapEnvSource);
        }
        EnvFrom.SecretEnvSource secretRef = envFrom.getSecretRef();
        if (Objects.nonNull(secretRef)) {
            V1SecretEnvSource v1SecretEnvSource = new V1SecretEnvSource();
            v1SecretEnvSource.setName(secretRef.getName());
            v1SecretEnvSource.setOptional(secretRef.getOptional());
            v1EnvFromSource.setSecretRef(v1SecretEnvSource);
        }
        return v1EnvFromSource;

    }

    public static List<V1EnvFromSource> build(List<EnvFrom> envFroms) {
        return envFroms.stream().map(EnvFromSourceBuilder::build).collect(Collectors.toList());
    }
}
