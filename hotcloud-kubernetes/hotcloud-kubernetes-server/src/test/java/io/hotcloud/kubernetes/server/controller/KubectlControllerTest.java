package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
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
    public void resourceListDelete() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        when(kubectlApi.delete(null,yaml)).thenReturn(Boolean.TRUE);
        String json = objectMapper.writeValueAsString(accepted(Boolean.TRUE).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().json(json, true));

    }

    @Test
    public void resourceListCreateOrReplace() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("resourceList.yaml");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        InputStream resourceAsStream = getClass().getResourceAsStream("resourceList-read.json");
        String deploymentReadJson = new BufferedReader(new InputStreamReader(resourceAsStream)).lines().collect(Collectors.joining());

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
