package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class KubectlClientIT extends ClientIntegrationTestBase {

    private static final String NAMESPACE = "default";
    private final Map<String, String> labelSelector = Map.of("k8s-app", "hotcloud");
    @Autowired
    private KubectlClient kubectlClient;
    @Autowired
    private PodClient podClient;

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
    public void uploadFileToPod_then_downloadDirectoryToLocally() throws InterruptedException {
        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);

        PodList readList = podClient.readList(NAMESPACE, labelSelector);
        List<Pod> pods = readList.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());

        Boolean uploaded = kubectlClient.upload(NAMESPACE, podNames.get(0), null, "/home/yaolianhua/.profile", "/hotcloud/.profile", CopyAction.FILE);
        Assertions.assertTrue(uploaded);

        Boolean downloaded = kubectlClient.download(NAMESPACE, podNames.get(0), null, "/hotcloud/config", "/home/yaolianhua/download_config", CopyAction.DIRECTORY);
        Assertions.assertTrue(downloaded);
    }

    @Test
    public void portForward() throws InterruptedException {
        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        PodList readList = podClient.readList(NAMESPACE, labelSelector);
        List<Pod> pods = readList.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());

        Boolean result = kubectlClient.portForward(NAMESPACE, podNames.get(0), null, 8080, 8078, null, null);
        Assertions.assertTrue(result);
    }

    @Test
    public void eventsRead() throws InterruptedException {
        log.info("Sleep 5s wait ...");
        TimeUnit.SECONDS.sleep(5);

        List<Event> events = kubectlClient.events(NAMESPACE);
        Map<String, String> nameMessages = events.stream()
                .collect(Collectors.toMap(e -> e.getMetadata().getName(), Event::getMessage));
        for (Map.Entry<String, String> entry : nameMessages.entrySet()) {
            log.info("Event name: {}, event message: {}", entry.getKey(), entry.getValue());
            Event eventResult = kubectlClient.events(NAMESPACE, entry.getKey());
            Assertions.assertEquals(entry.getValue(), eventResult.getMessage());
        }
    }

    @Test
    public void read() throws InterruptedException {

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        PodList readList = podClient.readList(NAMESPACE, labelSelector);
        List<Pod> pods = readList.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);
    }

    List<HasMetadata> apply() {

        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        List<HasMetadata> result = kubectlClient.resourceListCreateOrReplace(null, YamlBody.of(yaml));
        return result;

    }

    Boolean delete() {

        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        Boolean result = kubectlClient.delete(null, YamlBody.of(yaml));
        return result;

    }

}
