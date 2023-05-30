package io.hotcloud.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import org.jetbrains.annotations.Nullable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 * <p/>
 * Ensure that there is a configuration {@code $HOME/.kube/config} that can access the k8s cluster locally
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = KubernetesAgentApplication.class
)
@ActiveProfiles("kubernetes-integration-test-local")
public class ClientIntegrationTestBase {

    @Autowired
    private PodClient podClient;
    @Autowired
    private KubectlClient kubectlClient;

    protected void printExecCommandResult(String command) throws IOException, InterruptedException {
        System.out.println("------------------ " + command + " ------------------");
        Process process = Runtime.getRuntime().exec(command);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // 读取输出并打印到控制台
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待命令执行完毕
        int exitCode = process.waitFor();

        // 打印命令执行结果码
        System.out.println("------------------  Command exited with code: " + exitCode + " ------------------");
    }

    protected void printNamespacedEvents(String namespace, String resourceName) {
        System.out.println("--------------------- Print events --------------------");
        var events = kubectlClient.events(namespace)
                .stream()
                .filter(e -> e.getInvolvedObject().getName().contains(resourceName))
                .map(e -> String.format("%s\t\t%s\t\t\t\t%s\t\t\t\t\t%s", e.getType(), e.getReason(), e.getInvolvedObject().getKind() + "/" + e.getInvolvedObject().getName(), e.getMessage()))
                .collect(Collectors.joining("\n"));
        System.out.println("TYPE\t\tREASON\t\t\t\tOBJECT\t\t\t\t\t\t\t\t\t\tMESSAGE");
        System.out.println(events);

    }

    protected void waitPodRunningThenFetchContainerLogs(String namespace, String resourceName, String containerImage) {
        AtomicBoolean podHasCreated = new AtomicBoolean(false);
        String podName = null;
        while (!podHasCreated.get()) {

            podName = getPodName(namespace, resourceName, containerImage);

            if (podName == null || podName.isBlank()) {
                continue;
            }
            podHasCreated.set(true);
            System.out.println("------------------- Pod " + podName + " created ----------------------");
        }

        AtomicBoolean podHasRunning = new AtomicBoolean(false);
        while (!podHasRunning.get()) {

            boolean running = podIsRunning(namespace, podName);

            if (!running) {
                continue;
            }
            podHasRunning.set(true);
            System.out.println("------------------- Pod " + podName + " is running ----------------------");
        }

        String logResult = null;
        while (logResult == null || logResult.isBlank()) {
            try {
                logResult = podClient.logs(namespace, podName, 100);
            } catch (Exception e) {
                //
            }
        }

        System.out.println("--------------------- Pod " + podName + " log---------------------");
        System.out.println(logResult);
    }

    @Nullable
    protected String getPodName(String namespace, String resourceName, String containerImage) {
        String podName;
        podName = podClient.readList(namespace, null)
                .getItems()
                .stream()
                .filter(e -> e.getMetadata().getName().startsWith(resourceName) && e.getSpec().getContainers().get(0).getImage().contains(containerImage))
                .map(e -> e.getMetadata().getName())
                .findFirst()
                .orElse(null);
        return podName;
    }

    protected boolean podIsRunning(String namespace, String podName) {
        Pod pod = podClient.read(namespace, podName);
        if (Objects.isNull(pod)) {
            return false;
        }

        return "Running".equalsIgnoreCase(pod.getStatus().getPhase());
    }
}
