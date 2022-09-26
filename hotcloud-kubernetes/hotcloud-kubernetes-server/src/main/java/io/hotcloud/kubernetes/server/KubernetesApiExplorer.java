package io.hotcloud.kubernetes.server;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.hotcloud.kubernetes.api.AbstractKubernetesApi;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class KubernetesApiExplorer extends AbstractKubernetesApi {

    private final KubernetesProperties kubernetesProperties;

    public KubernetesApiExplorer(KubernetesProperties kubernetesProperties) {
        this.kubernetesProperties = kubernetesProperties;
    }

    @Override
    public ApiClient obtainApiClient() {

        boolean inCluster = kubernetesProperties.isInCluster();
        if (inCluster) {
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
            throw new RuntimeException(String.format("Build api-client error. '%s'", e.getMessage()), e);
        }

    }

    @Override
    public KubernetesClient obtainFabric8KubernetesClient() {

        boolean inCluster = kubernetesProperties.isInCluster();
        if (inCluster) {
            Config config = Config.autoConfigure(null);
            return new KubernetesClientBuilder().withConfig(config).build();
        }
        String kubeConfigPath = kubernetesProperties.getKubeConfigPath();
        String kubeconfig;
        try {
            List<String> lines = Files.readAllLines(Paths.get(kubeConfigPath));
            kubeconfig = String.join("\n", lines);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Read kubeconfig file error. path='%s' error='%s'", kubeConfigPath, e.getMessage()), e);
        }
        Config config = Config.fromKubeconfig(kubeconfig);

        return new KubernetesClientBuilder().withConfig(config).build();

    }
}
