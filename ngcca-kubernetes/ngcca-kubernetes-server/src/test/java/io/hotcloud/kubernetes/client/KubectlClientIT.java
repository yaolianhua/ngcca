package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
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
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
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
    public void metrics() {
        List<NodeMetrics> nodeMetrics = kubectlClient.topNodes();
        Assertions.assertTrue(nodeMetrics.size() > 0);
        printNodeMetrics(nodeMetrics);

        List<PodMetrics> podMetrics = kubectlClient.topPods();
        Assertions.assertTrue(podMetrics.size() > 0);
        printPodMetrics(podMetrics);
    }

    void printNodeMetrics(List<NodeMetrics> nodeMetrics) {
        System.out.println("--------------------- Print Node Metrics --------------------");
        System.out.printf("%50s%10s%10s%20s%10s%n", "NAME", "CPU（m）", "CPU%", "MEMORY（Mi）", "MEMORY%");
        for (NodeMetrics nodeMetric : nodeMetrics) {
            String node = nodeMetric.getMetadata().getName();
            long nodeCpuCapacity = Math.round(kubectlClient.getNode(node).getStatus().getCapacity().get("cpu").getNumericalAmount().doubleValue() * 1000);
            long nodeMemoryCapacity = Math.round(kubectlClient.getNode(node).getStatus().getCapacity().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));

            long cpu = Math.round(nodeMetric.getUsage().get("cpu").getNumericalAmount().doubleValue() * 1000);
            double cpuPercentage = (double) cpu / nodeCpuCapacity * 100;
            String cpuPercentageString = Double.parseDouble(new DecimalFormat("0.00").format(cpuPercentage)) + "%";

            long memory = Math.round(nodeMetric.getUsage().get("memory").getNumericalAmount().doubleValue() / (1024 * 1024));
            double memoryPercentage = (double) memory / nodeMemoryCapacity * 100;
            String memoryPercentageString = Double.parseDouble(new DecimalFormat("0.00").format(memoryPercentage)) + "%";

            System.out.printf("%50s%10s%10s%20s%10s%n", node, cpu, cpuPercentageString, memory, memoryPercentageString);
        }
    }

    void printPodMetrics(List<PodMetrics> podMetrics) {
        System.out.println("--------------------- Print Pod Metrics --------------------");
        System.out.printf("%20s%50s%10s%10s%n", "NAMESPACE", "NAME", "CPU（m）", "MEMORY（Mi）");
        for (PodMetrics podMetric : podMetrics) {
            String pod = podMetric.getMetadata().getName();
            String namespace = podMetric.getMetadata().getNamespace();

            Double cpu = podMetric.getContainers().stream()
                    .map(e -> e.getUsage().get("cpu").getNumericalAmount())
                    .map(BigDecimal::doubleValue)
                    .reduce(0.0, Double::sum);
            String cpuString = Math.round(cpu * 1000) + "m";

            Double memory = podMetric.getContainers().stream()
                    .map(e -> e.getUsage().get("memory").getNumericalAmount())
                    .map(BigDecimal::doubleValue)
                    .reduce(0.0, Double::sum);
            String memoryString = Math.round(memory / (1024 * 1024)) + "Mi";


            System.out.printf("%20s%50s%10s%10s%n", namespace, pod, cpuString, memoryString);
        }
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
