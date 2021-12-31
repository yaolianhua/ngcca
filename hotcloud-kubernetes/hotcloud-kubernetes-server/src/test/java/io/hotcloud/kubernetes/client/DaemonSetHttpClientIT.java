package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.IntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.DaemonSetHttpClient;
import io.hotcloud.kubernetes.model.LabelSelector;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.Toleration;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.Resources;
import io.hotcloud.kubernetes.model.pod.container.VolumeMount;
import io.hotcloud.kubernetes.model.volume.HostPathVolume;
import io.hotcloud.kubernetes.model.volume.Volume;
import io.hotcloud.kubernetes.model.workload.DaemonSetCreateRequest;
import io.hotcloud.kubernetes.model.workload.DaemonSetSpec;
import io.hotcloud.kubernetes.model.workload.DaemonSetTemplate;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class DaemonSetHttpClientIT extends IntegrationTestBase {

    private static final String DAEMONSET = "fluentd-elasticsearch";
    private static final String NAMESPACE = "default";
    @Autowired
    private DaemonSetHttpClient daemonSetHttpClient;

    @Before
    public void init() throws ApiException {
        create();
        log.info("DaemonSet Client Integration Test Start");
    }

    @After
    public void post() throws ApiException {
        daemonSetHttpClient.delete(NAMESPACE, DAEMONSET);
        log.info("DaemonSet Client Integration Test End");
    }

    @Test
    public void read() {
        Result<DaemonSetList> readList = daemonSetHttpClient.readList(NAMESPACE, null);
        List<DaemonSet> items = readList.getData().getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> daemonSetNames = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List DaemonSet Name: {}", daemonSetNames);

        Result<DaemonSet> result = daemonSetHttpClient.read(NAMESPACE, DAEMONSET);
        String name = result.getData().getMetadata().getName();
        Assert.assertEquals(name, DAEMONSET);
    }

    void create() throws ApiException {

        Map<String, String> labels = Map.of("k8s-app", "fluentd-logging");
        Map<String, String> matchLabels = Map.of("name", "fluentd-elasticsearch");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(DAEMONSET);
        metadata.setLabels(labels);

        DaemonSetSpec daemonSetSpec = new DaemonSetSpec();

        LabelSelector labelSelector = new LabelSelector();
        labelSelector.setMatchLabels(matchLabels);
        daemonSetSpec.setSelector(labelSelector);

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("quay.io/fluentd_elasticsearch/fluentd:v2.5.2");
        container.setName("fluentd-elasticsearch");

        Resources resources = new Resources();
        resources.setLimits(Map.of("memory", "200Mi"));
        resources.setRequests(Map.of("memory", "200Mi", "cpu", "100m"));
        container.setResources(resources);

        VolumeMount varlog = new VolumeMount();
        varlog.setName("varlog");
        varlog.setMountPath("/var/log");

        VolumeMount varlibdockercontainers = new VolumeMount();
        varlibdockercontainers.setName("varlibdockercontainers");
        varlibdockercontainers.setMountPath("/var/lib/docker/containers");
        varlibdockercontainers.setReadOnly(true);

        container.setVolumeMounts(List.of(varlog, varlibdockercontainers));
        spec.setContainers(List.of(container));

        Volume v1 = new Volume();
        HostPathVolume varlogHostPath = new HostPathVolume();
        varlogHostPath.setPath("/var/log");
        v1.setHostPath(varlogHostPath);
        v1.setName("varlog");

        Volume v2 = new Volume();
        HostPathVolume varlibdockercontainersHostPath = new HostPathVolume();
        varlibdockercontainersHostPath.setPath("/var/lib/docker/containers");
        v2.setHostPath(varlibdockercontainersHostPath);
        v2.setName("varlibdockercontainers");
        spec.setVolumes(List.of(v1, v2));

        spec.setTerminationGracePeriodSeconds(30L);

        Toleration toleration = new Toleration();
        toleration.setKey("node-role.kubernetes.io/master");
        toleration.setEffect(Toleration.Effect.NoExecute);
        toleration.setOperator(Toleration.Operator.Exists);

        spec.setTolerations(List.of(toleration));

        ObjectMetadata templateMetadata = new ObjectMetadata();
        templateMetadata.setLabels(matchLabels);

        DaemonSetTemplate template = new DaemonSetTemplate();
        template.setSpec(spec);
        template.setMetadata(templateMetadata);

        daemonSetSpec.setTemplate(template);

        DaemonSetCreateRequest request = new DaemonSetCreateRequest();
        request.setSpec(daemonSetSpec);
        request.setMetadata(metadata);

        daemonSetHttpClient.create(request);

    }

}
