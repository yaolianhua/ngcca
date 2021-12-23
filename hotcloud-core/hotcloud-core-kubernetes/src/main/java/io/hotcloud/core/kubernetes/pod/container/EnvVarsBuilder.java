package io.hotcloud.core.kubernetes.pod.container;

import io.kubernetes.client.openapi.models.V1ConfigMapKeySelector;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarSource;
import io.kubernetes.client.openapi.models.V1SecretKeySelector;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class EnvVarsBuilder {
    private EnvVarsBuilder() {
    }

    public static V1EnvVar build(Env env) {

        V1EnvVar v1EnvVar = new V1EnvVar();
        v1EnvVar.setName(env.getName());
        v1EnvVar.setValue(env.getValue());

        EnvSource envSource = env.getValueFrom();
        if (Objects.nonNull(envSource)) {
            V1EnvVarSource v1EnvVarSource = new V1EnvVarSource();
            EnvSource.ConfigMapKeySelector configMapKeyRef = envSource.getConfigMapKeyRef();
            if (Objects.nonNull(configMapKeyRef)) {
                V1ConfigMapKeySelector v1ConfigMapKeySelector = new V1ConfigMapKeySelector();
                v1ConfigMapKeySelector.setKey(configMapKeyRef.getKey());
                v1ConfigMapKeySelector.setName(configMapKeyRef.getName());
                v1ConfigMapKeySelector.setOptional(configMapKeyRef.getOptional());
                v1EnvVarSource.setConfigMapKeyRef(v1ConfigMapKeySelector);
                v1EnvVar.setValueFrom(v1EnvVarSource);
            }
            EnvSource.SecretKeySelector secretKeyRef = envSource.getSecretKeyRef();
            if (Objects.nonNull(secretKeyRef)) {
                V1SecretKeySelector v1SecretKeySelector = new V1SecretKeySelector();
                v1SecretKeySelector.setKey(secretKeyRef.getKey());
                v1SecretKeySelector.setName(secretKeyRef.getName());
                v1SecretKeySelector.setOptional(secretKeyRef.getOptional());
                v1EnvVarSource.setSecretKeyRef(v1SecretKeySelector);
                v1EnvVar.setValueFrom(v1EnvVarSource);
            }
        }
        return v1EnvVar;
    }

    public static List<V1EnvVar> build(List<Env> envs) {
        return envs.stream().map(EnvVarsBuilder::build).collect(Collectors.toList());
    }
}
