package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.StatefulSetClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EnableKubernetesAgentClient
public class StatefulSetClientIT extends ClientIntegrationTestBase {

    private static final String STATEFULSET = "web";
    private static final String NAMESPACE = "default";
    @Autowired
    private KubectlClient kubectlClient;
    @Autowired
    private StatefulSetClient statefulSetClient;

    @Before
    public void init() throws ApiException, IOException {
        apply();
        waitPodRunningThenFetchContainerLogs(NAMESPACE, STATEFULSET, "nginx:1.21.5");
    }

    @After
    public void post() throws ApiException, IOException {
        delete();
        printNamespacedEvents(NAMESPACE, STATEFULSET);
    }

    @Test
    public void read() throws InterruptedException {
        StatefulSetList readList = statefulSetClient.readList(NAMESPACE, null);
        List<StatefulSet> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName()).toList();
        names.forEach(System.out::println);

        StatefulSet result = statefulSetClient.read(NAMESPACE, STATEFULSET);
        String name = result.getMetadata().getName();
        Integer replicas = result.getSpec().getReplicas();
        Assert.assertEquals(STATEFULSET, name);
        Assert.assertEquals(1, (int) replicas);

    }

    void apply() throws IOException {
        String stringifyYaml;
        try (InputStream resource = this.getClass().getResourceAsStream("/statefulset.yaml")) {
            stringifyYaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource))).lines().collect(Collectors.joining("\n"));
        }

        kubectlClient.resourceListCreateOrReplace(NAMESPACE, YamlBody.of(stringifyYaml));
    }

    void delete() throws IOException {

        String stringifyYaml;
        try (InputStream resource = this.getClass().getResourceAsStream("/statefulset.yaml")) {
            stringifyYaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resource))).lines().collect(Collectors.joining("\n"));
        }

        kubectlClient.delete(NAMESPACE, YamlBody.of(stringifyYaml));

    }

}
