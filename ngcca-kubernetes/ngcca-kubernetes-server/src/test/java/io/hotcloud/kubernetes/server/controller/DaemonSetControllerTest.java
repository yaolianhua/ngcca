package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.*;
import io.hotcloud.kubernetes.api.DaemonSetApi;
import io.hotcloud.kubernetes.model.YamlBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = DaemonSetController.class)
@MockBeans(value = {
        @MockBean(classes = {
                DaemonSetApi.class
        })
})
public class DaemonSetControllerTest {

    public final static String PATH = "/v1/kubernetes/daemonsets";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DaemonSetApi daemonSetApi;

    @Test
    public void daemonSetDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{daemonSet}"), "default", "fluentd-elasticsearch"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(daemonSetApi, times(1)).delete("default", "fluentd-elasticsearch");
    }

    @Test
    public void daemonSetCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("daemonSet-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream daemonSetReadInputStream = getClass().getResourceAsStream("daemonSet-read.json");
        String daemonSetReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(daemonSetReadInputStream))).lines().collect(Collectors.joining());

        DaemonSet daemonSet = objectMapper.readValue(daemonSetReadJson, DaemonSet.class);
        when(daemonSetApi.create(yaml)).thenReturn(daemonSet);

        String json = objectMapper.writeValueAsString(daemonSet);

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void daemonSetRead() throws Exception {
        when(daemonSetApi.read("default", "fluentd-elasticsearch")).thenReturn(daemonSet());

        InputStream inputStream = getClass().getResourceAsStream("daemonSet-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        DaemonSet value = objectMapper.readValue(json, DaemonSet.class);
        String _json = objectMapper.writeValueAsString(value);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{daemonSet}"), "default", "fluentd-elasticsearch"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void daemonSetListRead() throws Exception {
        when(daemonSetApi.read("default", Map.of())).thenReturn(daemonSetList());

        InputStream inputStream = getClass().getResourceAsStream("daemonSetList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        StatefulSetList value = objectMapper.readValue(json, StatefulSetList.class);
        String _json = objectMapper.writeValueAsString(value);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public DaemonSetList daemonSetList() {

        DaemonSetListBuilder daemonSetListBuilder = new DaemonSetListBuilder();

        return daemonSetListBuilder.withApiVersion("apps/v1")
                .withKind("DaemonSetList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("101528695").build())
                .withItems(daemonSet())
                .build();
    }

    public DaemonSet daemonSet() {
        DaemonSetBuilder daemonSetBuilder = new DaemonSetBuilder();

        return daemonSetBuilder.withApiVersion("apps/v1")
                .withKind("DaemonSet")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("fluentd-elasticsearch")
                        .withNamespace("default")
                        .withLabels(Map.of("k8s-app", "fluentd-logging"))
                        .build())
                .withSpec(new DaemonSetSpecBuilder()
                        .withSelector(new LabelSelectorBuilder().withMatchLabels(Map.of("name", "fluentd-elasticsearch")).build())
                        .withUpdateStrategy(new DaemonSetUpdateStrategyBuilder()
                                .withType("RollingUpdate")
                                .withRollingUpdate(new RollingUpdateDaemonSetBuilder().withMaxSurge(new IntOrString(0)).withMaxUnavailable(new IntOrString(1)).build())
                                .build())
                        .withTemplate(new PodTemplateSpecBuilder()
                                .withMetadata(new ObjectMetaBuilder().withLabels(Map.of("name", "fluentd-elasticsearch")).build())
                                .withSpec(
                                        new PodSpecBuilder()
                                                .withContainers(
                                                        new ContainerBuilder()
                                                                .withImage("quay.io/fluentd_elasticsearch/fluentd:v2.5.2")
                                                                .withImagePullPolicy("IfNotPresent")
                                                                .withName("fluentd-elasticsearch")
                                                                .withResources(
                                                                        new ResourceRequirementsBuilder()
                                                                                .withLimits(Map.of("memory", Quantity.parse("200Mi")))
                                                                                .withRequests(Map.of("cpu", Quantity.parse("100m"), "memory", Quantity.parse("200Mi"))).build()
                                                                )
                                                                .withVolumeMounts(
                                                                        new VolumeMount[]{
                                                                                new VolumeMountBuilder().withMountPath("/var/log").withName("varlog").build(),
                                                                                new VolumeMountBuilder().withMountPath("/var/lib/docker/containers")
                                                                                        .withReadOnly(true)
                                                                                        .withName("varlibdockercontainers").build(),
                                                                        }
                                                                )
                                                                .build()
                                                )
                                                .withTerminationGracePeriodSeconds(30L)
                                                .withTolerations(new TolerationBuilder().withEffect("NoSchedule").withKey("node-role.kubernetes.io/master").withOperator("Exists").build())
                                                .withVolumes(
                                                        new VolumeBuilder().withName("varlog").withHostPath(new HostPathVolumeSourceBuilder().withPath("/var/log").withType("").build()).build(),
                                                        new VolumeBuilder().withName("varlibdockercontainers").withHostPath(new HostPathVolumeSourceBuilder().withPath("/var/lib/docker/containers").withType("").build()).build()
                                                )
                                                .build()

                                ).build()


                        ).build()


                ).build();
    }


}
