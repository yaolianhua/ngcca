package io.hotcloud.kubernetes.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.SecretApi;
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
import java.util.Collections;
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
@WebMvcTest(value = SecretController.class)
@MockBeans(value = {
        @MockBean(classes = {
                SecretApi.class
        })
})
public class SecretControllerTest {

    public final static String PATH = "/v1/kubernetes/secrets";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SecretApi secretApi;

    @Test
    public void secretCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("secret-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream secretReadInputStream = getClass().getResourceAsStream("secret-read.json");
        String secretReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(secretReadInputStream))).lines().collect(Collectors.joining());

        Secret secret = objectMapper.readValue(secretReadJson, Secret.class);
        when(secretApi.create(yaml)).thenReturn(secret);

        String json = objectMapper.writeValueAsString(secret);

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void secretDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{secret}"), "default", "regcred"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(secretApi, times(1)).delete("default", "regcred");
    }

    @Test
    public void secretRead() throws Exception {
        when(secretApi.read("default", "regcred"))
                .thenReturn(secret());

        InputStream inputStream = getClass().getResourceAsStream("secret-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        Secret value = objectMapper.readValue(json, Secret.class);
        String _json = objectMapper.writeValueAsString(value);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{secret}"), "default", "regcred"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public Secret secret() {
        SecretBuilder builder = new SecretBuilder();
        return builder.withImmutable(true)
                .withMetadata(new ObjectMetaBuilder().withName("regcred")
                        .withNamespace("default")
                        .build())
                .withData(Map.of(".dockerconfigjson", "eyJhdXRocyI6eyJoYXJib3IuY2xvdWQyZ28uY24iOnsidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiSGFyYm9yMTIzNDUiLCJhdXRoIjoiWVdSdGFXNDZTR0Z5WW05eU1USXpORFU9In19fQ=="))
                .withApiVersion("v1")
                .withKind("Secret")
                .withType("kubernetes.io/dockerconfigjson")
                .build();
    }

    @Test
    public void secretListRead() throws Exception {
        when(secretApi.read("default", Collections.emptyMap()))
                .thenReturn(secretList());

        InputStream inputStream = getClass().getResourceAsStream("secretList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());
        SecretList secretList = objectMapper.readValue(json, SecretList.class);
        String _json = objectMapper.writeValueAsString(secretList);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public SecretList secretList() {
        SecretListBuilder builder = new SecretListBuilder();

        return builder
                .withApiVersion("v1")
                .withKind("SecretList")
                .withItems(secret())
                .withMetadata(new ListMetaBuilder().withResourceVersion("71689").build())
                .build();
    }
}
