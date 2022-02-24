package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.configurations.SecretCreateApi;
import io.hotcloud.kubernetes.api.configurations.SecretDeleteApi;
import io.hotcloud.kubernetes.api.configurations.SecretReadApi;
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
import java.util.stream.Collectors;

import static io.hotcloud.kubernetes.server.WebResponse.created;
import static io.hotcloud.kubernetes.server.WebResponse.ok;
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
                SecretCreateApi.class,
                SecretReadApi.class,
                SecretDeleteApi.class
        })
})
public class SecretControllerTest {

    public final static String PATH = "/v1/kubernetes/secrets";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SecretCreateApi secretCreateApi;
    @MockBean
    private SecretReadApi secretReadApi;
    @MockBean
    private SecretDeleteApi secretDeleteApi;

    @Test
    public void secretCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("secret-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        InputStream secretReadInputStream = getClass().getResourceAsStream("secret-read.json");
        String secretReadJson = new BufferedReader(new InputStreamReader(secretReadInputStream)).lines().collect(Collectors.joining());

        Secret secret = objectMapper.readValue(secretReadJson, Secret.class);
        when(secretCreateApi.secret(yaml)).thenReturn(secret);

        String json = objectMapper.writeValueAsString(created(secret).getBody());

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
        verify(secretDeleteApi, times(1)).delete("default", "regcred");
    }

    @Test
    public void secretRead() throws Exception {
        when(secretReadApi.read("default", "regcred"))
                .thenReturn(secret());

        InputStream inputStream = getClass().getResourceAsStream("secret-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Secret value = objectMapper.readValue(json, Secret.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{secret}"), "default", "regcred"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public Secret secret() {
        SecretBuilder builder = new SecretBuilder();
        Secret secret = builder.withImmutable(true)
                .withMetadata(new ObjectMetaBuilder().withName("regcred")
                        .withNamespace("default")
                        .build())
                .withData(Map.of(".dockerconfigjson", "eyJhdXRocyI6eyJoYXJib3IuY2xvdWQyZ28uY24iOnsidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiSGFyYm9yMTIzNDUiLCJhdXRoIjoiWVdSdGFXNDZTR0Z5WW05eU1USXpORFU9In19fQ=="))
                .withApiVersion("v1")
                .withKind("Secret")
                .withType("kubernetes.io/dockerconfigjson")
                .build();
        return secret;
    }

    @Test
    public void secretListRead() throws Exception {
        when(secretReadApi.read("default", Collections.emptyMap()))
                .thenReturn(secretList());

        InputStream inputStream = getClass().getResourceAsStream("secretList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        SecretList secretList = objectMapper.readValue(json, SecretList.class);
        String _json = objectMapper.writeValueAsString(ok(secretList).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public SecretList secretList() {
        SecretListBuilder builder = new SecretListBuilder();

        SecretList secretList = builder
                .withApiVersion("v1")
                .withKind("SecretList")
                .withItems(secret())
                .withMetadata(new ListMetaBuilder().withResourceVersion("71689").build())
                .build();
        return secretList;
    }
}
