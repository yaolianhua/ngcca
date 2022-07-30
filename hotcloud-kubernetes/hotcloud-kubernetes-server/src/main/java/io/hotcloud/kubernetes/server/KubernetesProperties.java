package io.hotcloud.kubernetes.server;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "kubernetes")
@Slf4j
@Data
@Properties(prefix = "kubernetes")
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

    /**
     * enable global-event watch event for workloads, default is {@code false}
     */
    private boolean enableWorkloadsWatcher;

    public static String defaultKubeconfigPath() {
        return String.format("%s/.kube/config", System.getenv("HOME"));
    }

    @PostConstruct
    public void log() {
        if (this.enableWorkloadsWatcher) {
            Log.info(KubernetesProperties.class.getName(), "【Load Kubernetes Properties】enable global event watch for workloads ");
        }
        if (this.inCluster) {
            Log.info(KubernetesProperties.class.getName(), "【Load Kubernetes Properties】using in-cluster mode ");
            return;
        }
        Log.info(KubernetesProperties.class.getName(), String.format("【Load Kubernetes Properties】using kubeconfig path '%s'", kubeConfigPath));
    }


}
