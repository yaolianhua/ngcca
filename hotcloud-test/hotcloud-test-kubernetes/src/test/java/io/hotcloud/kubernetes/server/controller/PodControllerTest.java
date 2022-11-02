package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.pod.PodApi;
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

import static io.hotcloud.common.api.WebResponse.created;
import static io.hotcloud.common.api.WebResponse.ok;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = PodController.class)
@MockBeans(value = {
        @MockBean(classes = {
                PodApi.class
        })
})
public class PodControllerTest {

    public final static String PATH = "/v1/kubernetes/pods";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PodApi podApi;

    @Test
    public void annotations() throws Exception {
        String contentBody = objectMapper.writeValueAsString(Map.of("icon-url", "http://goo.gl/XXBTWq"));
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH.concat("/{namespace}/{pod}/annotations"), "default", "nginx")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentBody))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(podApi, times(1)).addAnnotations("default", "nginx", Map.of("icon-url", "http://goo.gl/XXBTWq"));
    }

    @Test
    public void labels() throws Exception {
        String contentBody = objectMapper.writeValueAsString(Map.of("k8s-app", "nginx"));
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH.concat("/{namespace}/{pod}/labels"), "default", "nginx")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentBody))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(podApi, times(1)).addLabels("default", "nginx", Map.of("k8s-app", "nginx"));
    }

    @Test
    public void podDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{pod}"), "default", "nginx"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(podApi, times(1)).delete("default", "nginx");
    }

    @Test
    public void podCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("pod-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        InputStream podReadInputStream = getClass().getResourceAsStream("pod-read.json");
        String podReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(podReadInputStream))).lines().collect(Collectors.joining());

        Pod pod = objectMapper.readValue(podReadJson, Pod.class);
        when(podApi.create(yaml)).thenReturn(pod);

        String json = objectMapper.writeValueAsString(created(pod).getBody());


        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void podRead() throws Exception {
        when(podApi.read("default", "nginx")).thenReturn(pod());

        InputStream inputStream = getClass().getResourceAsStream("pod-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        Pod value = objectMapper.readValue(json, Pod.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{pod}"), "default", "nginx"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void podListRead() throws Exception {
        when(podApi.read("default", Map.of())).thenReturn(podList());

        InputStream inputStream = getClass().getResourceAsStream("podList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        PodList value = objectMapper.readValue(json, PodList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public PodList podList() {

        PodListBuilder podListBuilder = new PodListBuilder();

        return podListBuilder.withApiVersion("v1")
                .withKind("PodList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("65012").build())
                .withItems(pod())
                .build();
    }

    public Pod pod() {
        PodBuilder podBuilder = new PodBuilder();

        return podBuilder.withApiVersion("v1")
                .withKind("Pod")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("nginx")
                        .withNamespace("default")
                        .build())
                .withSpec(new PodSpecBuilder().withContainers(
                        new ContainerBuilder().withImage("nginx:1.14.2")
                                .withImagePullPolicy("IfNotPresent").withName("nginx")
                                .withPorts(new ContainerPortBuilder().withContainerPort(80).withProtocol("TCP").build())
                                .build()
                ).build()).build();
    }


}
