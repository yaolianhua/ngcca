package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.PodHttpClient;
import io.hotcloud.kubernetes.client.workload.StatefulSetHttpClient;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.Port;
import io.hotcloud.kubernetes.model.pod.container.VolumeMount;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimSpec;
import io.hotcloud.kubernetes.model.workload.StatefulSetCreateRequest;
import io.hotcloud.kubernetes.model.workload.StatefulSetSpec;
import io.hotcloud.kubernetes.model.workload.StatefulSetTemplate;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class StatefulSetHttpClientIT extends ClientIntegrationTestBase {

    private static final String STATEFULSET = "web";
    private static final String NAMESPACE = "default";
    @Autowired
    private StatefulSetHttpClient statefulSetHttpClient;
    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("StatefulSet Client Integration Test Start");
        create();
        log.info("Create StatefulSet Name: '{}'", STATEFULSET);
    }

    @After
    public void post() throws ApiException {
        statefulSetHttpClient.delete(NAMESPACE, STATEFULSET);
        log.info("Delete StatefulSet Name: '{}'", STATEFULSET);
        log.info("StatefulSet Client Integration Test End");
    }

    @Test
    public void read() throws InterruptedException {
        StatefulSetList readList = statefulSetHttpClient.readList(NAMESPACE, null);
        List<StatefulSet> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List StatefulSet Name: {}", names);

        StatefulSet result = statefulSetHttpClient.read(NAMESPACE, STATEFULSET);
        String name = result.getMetadata().getName();
        Integer replicas = result.getSpec().getReplicas();
        Assert.assertEquals(name, STATEFULSET);
        Assert.assertEquals(2, (int) replicas);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        PodList podListResult = podHttpClient.readList(NAMESPACE, null);
        List<Pod> pods = podListResult.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .filter(e -> e.startsWith(STATEFULSET))
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);
    }

    void create() throws ApiException {

        Map<String, String> matchLabels = Map.of("app", "nginx");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(STATEFULSET);

        StatefulSetSpec statefulSetSpec = new StatefulSetSpec();

        LabelSelector labelSelector = new LabelSelector();
        labelSelector.setMatchLabels(matchLabels);
        statefulSetSpec.setSelector(labelSelector);
        statefulSetSpec.setReplicas(2);
        statefulSetSpec.setServiceName("nginx");

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("nginx");
        container.setName("nginx");

        Port port = new Port();
        port.setContainerPort(80);
        port.setName("web");
        container.setPorts(List.of(port));

        VolumeMount www = new VolumeMount();
        www.setName("www");
        www.setMountPath("/usr/share/nginx/html");

        container.setVolumeMounts(List.of(www));
        spec.setContainers(List.of(container));


        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(matchLabels);

        StatefulSetTemplate template = new StatefulSetTemplate();
        template.setSpec(spec);
        template.setMetadata(templateMetadata);

        statefulSetSpec.setTemplate(template);

        PersistentVolumeClaimCreateRequest volumeClaimTemplate = new PersistentVolumeClaimCreateRequest();
        ObjectMetadata claimMetadata = new ObjectMetadata();
        claimMetadata.setName("www");

        PersistentVolumeClaimSpec volumeClaimSpec = new PersistentVolumeClaimSpec();
        volumeClaimSpec.setAccessModes(List.of("ReadWriteOnce"));
        Resources resources = new Resources();
        resources.setRequests(Map.of("storage", "1Gi"));
        volumeClaimSpec.setResources(resources);

        volumeClaimTemplate.setSpec(volumeClaimSpec);

        volumeClaimTemplate.setMetadata(claimMetadata);
        statefulSetSpec.setVolumeClaimTemplates(List.of(volumeClaimTemplate));

        StatefulSetCreateRequest request = new StatefulSetCreateRequest();
        request.setSpec(statefulSetSpec);
        request.setMetadata(metadata);

        statefulSetHttpClient.create(request);

    }

}
