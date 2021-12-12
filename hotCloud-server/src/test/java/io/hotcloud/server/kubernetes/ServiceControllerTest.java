package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.core.kubernetes.service.ServiceCreateApi;
import io.hotcloud.core.kubernetes.service.ServiceDeleteApi;
import io.hotcloud.core.kubernetes.service.ServiceReadApi;
import io.hotcloud.server.kubernetes.controller.ServiceController;
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
@WebMvcTest(value = ServiceController.class)
@MockBeans(value = {
        @MockBean(classes = {
                ServiceDeleteApi.class,
                ServiceReadApi.class,
                ServiceCreateApi.class
        })
})
public class ServiceControllerTest {

    public final static String PATH = "/v1/kubernetes/services";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ServiceReadApi serviceReadApi;
    @MockBean
    private ServiceCreateApi serviceCreateApi;
    @MockBean
    private ServiceDeleteApi serviceDeleteApi;

    @Test
    public void serviceDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{service}"), "default", "hotcloud"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(serviceDeleteApi, times(1)).delete("default", "hotcloud");
    }

    @Test
    public void serviceCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("service-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        InputStream serviceReadInputStream = getClass().getResourceAsStream("service-read.json");
        String serviceReadJson = new BufferedReader(new InputStreamReader(serviceReadInputStream))
                .lines()
                .collect(Collectors.joining());

        Service service = objectMapper.readValue(serviceReadJson, Service.class);
        when(serviceCreateApi.service(yaml)).thenReturn(service);

        String json = objectMapper.writeValueAsString(created(service).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.TEXT_PLAIN_VALUE).content(yaml))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void serviceRead() throws Exception {
        when(serviceReadApi.read("default", "hotcloud")).thenReturn(service());

        InputStream inputStream = getClass().getResourceAsStream("service-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Service value = objectMapper.readValue(json, Service.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{service}"), "default", "hotcloud"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void serviceListRead() throws Exception {
        when(serviceReadApi.read("default", Map.of())).thenReturn(serviceList());

        InputStream inputStream = getClass().getResourceAsStream("serviceList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        ServiceList value = objectMapper.readValue(json, ServiceList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        String body = objectMapper.writeValueAsString(Map.of());
        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default").contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public ServiceList serviceList() {

        ServiceListBuilder serviceListBuilder = new ServiceListBuilder();
        ServiceList serviceList = serviceListBuilder.withApiVersion("v1")
                .withKind("ServiceList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("221388").build())
                .withItems(service())
                .build();

        return serviceList;
    }

    public Service service() {
        ServiceBuilder serviceBuilder = new ServiceBuilder();

        Service service = serviceBuilder.withApiVersion("v1")
                .withKind("Service")
                .withMetadata(new ObjectMetaBuilder().withLabels(Map.of("k8s-app", "hotcloud")).withName("hotcloud").withNamespace("default").build())
                .withSpec(new ServiceSpecBuilder()
                        .withClusterIP("10.107.236.4")
                        .withPorts(new ServicePortBuilder().withName("http").withNodePort(30000).withPort(8080).withProtocol("TCP").withTargetPort(new IntOrString("http")).build())
                        .withSelector(Map.of("k8s-app", "hotcloud"))
                        .withSessionAffinity("None")
                        .withType("NodePort")
                        .build())
                .build();

        return service;
    }


}
