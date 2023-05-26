package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.YamlBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@EnableKubernetesAgentClient
public class KubectlClientIT extends ClientIntegrationTestBase {

    private static final String NAMESPACE = "default";
    @Autowired
    private KubectlClient kubectlClient;

    @Before
    public void init() throws Exception {
        //
        apply();
        //
        waitPodRunningThenFetchContainerLogs(NAMESPACE, "nginx", "nginx:1.21.5");
    }

    @After
    public void post() throws Exception {
        //
        delete();
        //
        printNamespacedEvents(NAMESPACE, "nginx");
    }

    @Test
    public void allinone() {

        try {
            //port forward
            Boolean result = kubectlClient.portForward(NAMESPACE, "nginx", null, 80, 8180, 1L, TimeUnit.MINUTES);
            Assertions.assertTrue(result);
        } catch (Exception e) {
            log.error("port forward error: {}", e.getMessage());
        }

        try {
            printExecCommandResult("curl http://127.0.0.1:8180 -v");
        } catch (IOException e) {
            log.error("Exec command error: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //upload to pod
        String uploadFilePath = Path.of(System.getProperty("user.home"), ".ssh", "config").toString();
        String targetPodPath = "/tmp/config";
        log.info("upload local file {} to pod {}", uploadFilePath, targetPodPath);
        try {
            Boolean uploaded = kubectlClient.upload(NAMESPACE, "nginx", "nginx", uploadFilePath, targetPodPath, CopyAction.FILE);
            Assertions.assertTrue(uploaded);
        } catch (Exception e) {
            log.error("upload error: {}", e.getMessage());
        }


        //download from pod
        String downloadLocalPath = Path.of("/tmp").toString();
        String targetPodDir = Path.of("/etc/nginx/conf.d").toString();
        log.info("download pod dir {} to local {}", targetPodDir, downloadLocalPath);
        try {
            Boolean downloaded = kubectlClient.download(NAMESPACE, "nginx", null, targetPodDir, downloadLocalPath, CopyAction.DIRECTORY);
            Assertions.assertTrue(downloaded);
        } catch (Exception e) {
            log.error("download error: {}", e.getMessage());
        }

    }

    void apply() throws IOException {
        String stringifyYaml;
        try (InputStream resource = this.getClass().getResourceAsStream("/pod-nginx.yaml")) {
            stringifyYaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource))).lines().collect(Collectors.joining("\n"));
        }

        kubectlClient.resourceListCreateOrReplace(NAMESPACE, YamlBody.of(stringifyYaml));
    }

    void delete() throws IOException {

        String stringifyYaml;
        try (InputStream resource = this.getClass().getResourceAsStream("/pod-nginx.yaml")) {
            stringifyYaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource))).lines().collect(Collectors.joining("\n"));
        }

        kubectlClient.delete(NAMESPACE, YamlBody.of(stringifyYaml));

    }

}
