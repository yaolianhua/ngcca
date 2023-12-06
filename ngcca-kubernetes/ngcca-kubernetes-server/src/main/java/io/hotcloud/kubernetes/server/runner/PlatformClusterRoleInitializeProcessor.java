package io.hotcloud.kubernetes.server.runner;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.KubectlApi;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class PlatformClusterRoleInitializeProcessor implements ApplicationRunner {

    private final KubectlApi kubectlApi;

    public PlatformClusterRoleInitializeProcessor(KubectlApi kubectlApi) {
        this.kubectlApi = kubectlApi;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InputStream inputStream = new ClassPathResource("clusterrole.yaml").getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String yaml = bufferedReader
                .lines()
                .collect(Collectors.joining("\n"));

        kubectlApi.apply(null, yaml);
        Log.info(this, null, Event.START, "init ClusterRole ClusterRoleBinding ServiceAccount success");
    }
}
