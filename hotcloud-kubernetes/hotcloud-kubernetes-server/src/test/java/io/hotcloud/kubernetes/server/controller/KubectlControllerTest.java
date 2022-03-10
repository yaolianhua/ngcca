package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.model.YamlBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.hotcloud.kubernetes.server.WebResponse.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = KubectlController.class)
@MockBeans(value = {
        @MockBean(classes = {
                KubectlApi.class
        })
})
public class KubectlControllerTest {

    public final static String PATH = "/v1/kubernetes/equivalents";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private KubectlApi kubectlApi;

    @Test
    public void eventsRead() throws Exception {
        InputStream resourceAsStream = getClass().getResourceAsStream("events-read.json");
        String readJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream))).lines().collect(Collectors.joining());

        Event events = objectMapper.readValue(readJson, Event.class);
        when(kubectlApi.events("default", "hotcloud-66459b5b74-zf6sg.16daf5a47394186e")).thenReturn(events);

        String json = objectMapper.writeValueAsString(ok(events).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/events/{name}"), "default", "hotcloud-66459b5b74-zf6sg.16daf5a47394186e"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json, true));
    }

    @Test
    public void eventsListRead() throws Exception {
        InputStream resourceAsStream = getClass().getResourceAsStream("events-list-read.json");
        String readJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream))).lines().collect(Collectors.joining());

        @SuppressWarnings("unchecked")
        List<Event> events = objectMapper.readValue(readJson, List.class);
        when(kubectlApi.events("default")).thenReturn(events);

        String json = objectMapper.writeValueAsString(ok(events).getBody());


        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/events"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json, true));
    }

    @Test
    public void portForward() throws Exception {
        when(kubectlApi.portForward("middleware", "redisinsight-6b8658f8cf-fl754", "127.0.0.1", 8001, 8001, 30L, TimeUnit.SECONDS))
                .thenReturn(Boolean.TRUE);

        LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        Map<String, String> params = Map.of("ipv4Address", "127.0.0.1",
                "containerPort", String.valueOf(8001),
                "localPort", String.valueOf(8001),
                "alive", String.valueOf(30L),
                "timeUnit", TimeUnit.SECONDS.name());
        multiValueMap.setAll(params);

        this.mockMvc.perform(MockMvcRequestBuilders.post(PATH.concat("/{namespace}/{pod}/forward"), "middleware", "redisinsight-6b8658f8cf-fl754")
                        .params(multiValueMap))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(kubectlApi, times(1)).portForward("middleware", "redisinsight-6b8658f8cf-fl754", "127.0.0.1", 8001, 8001, 30L, TimeUnit.SECONDS);
    }

    @Test
    public void resourceListDelete() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        when(kubectlApi.delete(null, yaml)).thenReturn(Boolean.TRUE);
        String json = objectMapper.writeValueAsString(accepted(Boolean.TRUE).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json(json, true));

    }

    @Test
    public void resourceListCreateOrReplace() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        InputStream resourceAsStream = getClass().getResourceAsStream("resourceList-read.json");
        String deploymentReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream))).lines().collect(Collectors.joining());

        @SuppressWarnings("unchecked")
        List<HasMetadata> hasMetadata = objectMapper.readValue(deploymentReadJson, List.class);
        when(kubectlApi.apply(null,yaml)).thenReturn(hasMetadata);

        String json = objectMapper.writeValueAsString(created(hasMetadata).getBody());


        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }


}
