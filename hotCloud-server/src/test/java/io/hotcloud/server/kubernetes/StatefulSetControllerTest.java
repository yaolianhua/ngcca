package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.*;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.StatefulSetCreateApi;
import io.hotcloud.core.kubernetes.workload.StatefulSetDeleteApi;
import io.hotcloud.core.kubernetes.workload.StatefulSetReadApi;
import io.hotcloud.server.kubernetes.controller.StatefulSetController;
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
import java.util.stream.Collectors;

import static io.hotcloud.server.WebResponse.created;
import static io.hotcloud.server.WebResponse.ok;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = StatefulSetController.class)
@MockBeans(value = {
        @MockBean(classes = {
                StatefulSetCreateApi.class,
                StatefulSetDeleteApi.class,
                StatefulSetReadApi.class
        })
})
public class StatefulSetControllerTest {

    public final static String PATH = "/v1/kubernetes/statefulsets";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StatefulSetCreateApi statefulSetCreateApi;
    @MockBean
    private StatefulSetReadApi statefulSetReadApi;
    @MockBean
    private StatefulSetDeleteApi statefulSetDeleteApi;

    @Test
    public void statefulSetDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{statefulSet}"), "default", "web"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(statefulSetDeleteApi, times(1)).delete("default", "web");
    }

    @Test
    public void statefulSetCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("statefulSet-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        InputStream statefulSetReadInputStream = getClass().getResourceAsStream("statefulSet-read.json");
        String statefulSetReadJson = new BufferedReader(new InputStreamReader(statefulSetReadInputStream)).lines().collect(Collectors.joining());

        StatefulSet statefulSet = objectMapper.readValue(statefulSetReadJson, StatefulSet.class);
        when(statefulSetCreateApi.statefulSet(yaml)).thenReturn(statefulSet);

        String json = objectMapper.writeValueAsString(created(statefulSet).getBody());

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void statefulSetRead() throws Exception {
        when(statefulSetReadApi.read("default", "web")).thenReturn(statefulSet());

        InputStream inputStream = getClass().getResourceAsStream("statefulSet-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        StatefulSet value = objectMapper.readValue(json, StatefulSet.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{statefulSet}"), "default", "web"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void statefulSetListRead() throws Exception {
        when(statefulSetReadApi.read("default", Map.of())).thenReturn(statefulSetList());

        InputStream inputStream = getClass().getResourceAsStream("statefulSetList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        StatefulSetList value = objectMapper.readValue(json, StatefulSetList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        String body = objectMapper.writeValueAsString(Map.of());
        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default").contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public StatefulSetList statefulSetList() {

        StatefulSetListBuilder statefulSetListBuilder = new StatefulSetListBuilder();
        StatefulSetList statefulSetList = statefulSetListBuilder.withApiVersion("apps/v1")
                .withKind("StatefulSetList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("6037").build())
                .withItems(statefulSet())
                .build();

        return statefulSetList;
    }

    public StatefulSet statefulSet() {
        StatefulSetBuilder statefulSetBuilder = new StatefulSetBuilder();

        StatefulSet statefulSet = statefulSetBuilder.withApiVersion("apps/v1")
                .withKind("StatefulSet")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("web")
                        .withNamespace("default")
                        .build())
                .withSpec(new StatefulSetSpecBuilder()
                        .withReplicas(2)
                        .withSelector(new LabelSelectorBuilder().withMatchLabels(Map.of("app", "nginx")).build())
                        .withServiceName("nginx")
                        .withUpdateStrategy(new StatefulSetUpdateStrategyBuilder()
                                .withType("RollingUpdate")
                                .withRollingUpdate(new RollingUpdateStatefulSetStrategyBuilder().withPartition(0).build())
                                .build())
                        .withVolumeClaimTemplates(
                                new PersistentVolumeClaimBuilder().withApiVersion("v1")
                                        .withKind("PersistentVolumeClaim")
                                        .withMetadata(new ObjectMetaBuilder().withName("www").build())
                                        .withSpec(new PersistentVolumeClaimSpecBuilder()
                                                .withAccessModes("ReadWriteOnce")
                                                .withResources(new ResourceRequirementsBuilder().withRequests(Map.of("storage", Quantity.parse("1Gi"))).build())
                                                .withVolumeMode("Filesystem")
                                                .build())
                                        .build()
                        )
                        .withTemplate(new PodTemplateSpecBuilder()
                                .withMetadata(new ObjectMetaBuilder().withLabels(Map.of("app", "nginx")).build())
                                .withSpec(
                                        new PodSpecBuilder()
                                                .withContainers(
                                                        new ContainerBuilder()
                                                                .withImage("k8s.gcr.io/nginx-slim:0.8")
                                                                .withImagePullPolicy("IfNotPresent")
                                                                .withName("nginx")
                                                                .withPorts(new ContainerPortBuilder()
                                                                        .withContainerPort(80)
                                                                        .withName("web")
                                                                        .withProtocol("TCP")
                                                                        .build())
                                                                .withVolumeMounts(
                                                                        new VolumeMount[]{
                                                                                new VolumeMountBuilder().withMountPath("/usr/share/nginx/html").withName("www").build()
                                                                        }
                                                                )
                                                                .build()
                                                )
                                                .build()

                                ).build()


                        ).build()


                ).build();

        return statefulSet;
    }


}
