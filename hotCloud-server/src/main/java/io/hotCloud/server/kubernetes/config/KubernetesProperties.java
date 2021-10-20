package io.hotCloud.server.kubernetes.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "kubernetes")
@Slf4j
@Data
public class KubernetesProperties {

    /**
     * kube config path
     */
    private String kubeConfigPath = defaultKubeconfigPath();
    /**
     * in-cluster mode
     */
    private boolean inCluster = true;

    public static String defaultKubeconfigPath() {
        return String.format("%s/.kube/config", System.getenv("HOME"));
    }

    @PostConstruct
    public void log() {
        if (this.inCluster) {
            log.info("【Load Kubernetes Configuration】using in-cluster mode ");
            return;
        }

        log.info("【Load Kubernetes Configuration】using kubeconfig path '{}'", kubeConfigPath);


    }


}
