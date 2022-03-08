package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.equivalent.KubectlHttpClient;
import io.hotcloud.kubernetes.client.workload.PodHttpClient;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableHotCloudHttpClient
public class KubectlHttpClientIT extends ClientIntegrationTestBase {

    private static final String NAMESPACE = "default";
    private final Map<String, String> labelSelector = Map.of("k8s-app", "hotcloud");
    @Autowired
    private KubectlHttpClient kubectlHttpClient;
    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws Exception {
        log.info("Kubectl Integration Test Start");
        List<HasMetadata> hasMetadata = apply();

        for (HasMetadata metadata : hasMetadata) {
            log.info("{} '{}' create or replace", metadata.getKind(), metadata.getMetadata().getName());
        }
    }

    @After
    public void post() throws Exception {
        Boolean delete = delete();
        log.info("ResourceList deleted success='{}'", delete);

        log.info("Kubectl Integration Test End");
    }

    @Test
    public void read() throws InterruptedException {

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        Result<PodList> readList = podHttpClient.readList(NAMESPACE, labelSelector);
        List<Pod> pods = readList.getData().getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);
    }

    List<HasMetadata> apply() {

        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Result<List<HasMetadata>> result = kubectlHttpClient.resourceListCreateOrReplace(null, YamlBody.of(yaml));
        return result.getData();

    }

    Boolean delete() {

        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        Result<Boolean> result = kubectlHttpClient.delete(null, YamlBody.of(yaml));
        return result.getData();

    }

}
