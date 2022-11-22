package io.hotcloud.kubernetes.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "kubernetes")
@Slf4j
@Data
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

    private RabbitmqProperties rabbitmq;

    public static String defaultKubeconfigPath() {
        return String.format("%s/.kube/config", System.getenv("HOME"));
    }

    @PostConstruct
    public void log() {
        if (this.enableWorkloadsWatcher) {
            Assert.notNull(rabbitmq, "rabbitmq config is null");
            String rabbitmqUrl = String.format("amqp://%s@%s:%s", rabbitmq.getUsername(), rabbitmq.getHost(), rabbitmq.getPort());
            log.info("【Load Kubernetes Properties】enable global event watch for workloads. rabbitmq url '{}'", rabbitmqUrl);
        }
        if (this.inCluster) {
            log.info("【Load Kubernetes Properties】using in-cluster mode ");
            return;
        }
        log.info(String.format("【Load Kubernetes Properties】using kubeconfig path '%s'", kubeConfigPath));
    }

    @Data
    public static class RabbitmqProperties {
        private String host;
        private Integer port;
        private String username;
        private String password;
    }

}
