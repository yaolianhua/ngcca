package io.hotCloud.server.kubernetes.config;

import io.hotCloud.core.kubernetes.AbstractKubernetesApi;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class KubernetesApiClientExplorer extends AbstractKubernetesApi {

    private final KubernetesProperties kubernetesProperties;

    public KubernetesApiClientExplorer(KubernetesProperties kubernetesProperties) {
        this.kubernetesProperties = kubernetesProperties;
    }

    @Override
    public ApiClient obtainClient() {

        boolean inCluster = kubernetesProperties.isInCluster();
        if (inCluster){
            log.info("Api client is using in-cluster mode");
            // loading the in-cluster config, including:
            //   1. service-account CA
            //   2. service-account bearer-token
            //   3. service-account namespace
            //   4. master endpoints(ip, port) from pre-set environment variables
            try {
                return ClientBuilder.cluster().build();
            } catch (IOException e) {
                throw new RuntimeException(String.format("Build api-client error. '%s'",e.getMessage()),e);
            }
        }

        // file path to your KubeConfig
        String kubeconfigPath = kubernetesProperties.getKubeConfigPath();

        // loading the out-of-cluster config, a kubeconfig from file-system
        try {
            FileReader fileReader = new FileReader(kubeconfigPath);
            KubeConfig kubeConfig = KubeConfig.loadKubeConfig(fileReader);
            ApiClient client = ClientBuilder.kubeconfig(kubeConfig).build();

            return client;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Build api-client error. '%s'",e.getMessage()),e);
        }

    }
}
