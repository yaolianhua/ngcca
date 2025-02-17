package io.hotcloud.kubernetes.service;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.Properties;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "kubernetes")
@Data
@Properties(prefix = CONFIG_PREFIX + "kubernetes")
public class KubernetesProperties {

    /**
     * kube config path, default is {@code $HOME/.kube/config}
     */
    private String kubeConfigPath = defaultKubeconfigPath();
    /**
     * in-cluster mode, default is {@code true}
     */
    private boolean inCluster = true;

    public static String defaultKubeconfigPath() {
        return String.format("%s/.kube/config", System.getenv("HOME"));
    }

    @PostConstruct
    public void log() {
        Log.info(this, this, Event.START, "load kubernetes properties");
    }

}
