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
    private String kubeConfigPath = String.format("%s/.kube/config",System.getenv("HOME"));
    /**
     * in-cluster mode
     */
    private boolean inCluster = true;

    @PostConstruct
    public void log(){
        log.info("【Load Kubernetes Configuration】in-cluster mode '{}', the kube config path is '{}'",inCluster,kubeConfigPath);
    }


}
