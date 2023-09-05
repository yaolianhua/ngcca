package io.hotcloud.kubernetes.server.config;

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

    public static final String ENABLE_WORKLOADS_WATCHER = "kubernetes.enable-workloads-watcher";

    /**
     * kube config path, default is {@code $HOME/.kube/config}
     */
    private String kubeConfigPath = defaultKubeconfigPath();
    /**
     * in-cluster mode, default is {@code true}
     */
    private boolean inCluster = true;

    private String clusterId = "default-cluster";

    /**
     * enable global-event watch event for workloads, default is {@code false}
     */
    private boolean enableWorkloadsWatcher;

    public static String defaultKubeconfigPath() {
        return String.format("%s/.kube/config", System.getenv("HOME"));
    }

    @PostConstruct
    public void log() {
        Log.info(this, this, Event.START, "load kubernetes properties");
    }

}
