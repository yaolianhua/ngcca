package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.core.kubernetes.secret.SecretCreateApi;
import io.hotcloud.core.kubernetes.secret.SecretDeleteApi;
import io.hotcloud.core.kubernetes.secret.SecretReadApi;
import io.hotcloud.server.kubernetes.controller.SecretController;
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

import static io.hotcloud.server.WebResponse.created;
import static io.hotcloud.server.WebResponse.ok;
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
        InputStream inputStream = getClass().getResourceAsStream("secret-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        InputStream secretReadInputStream = getClass().getResourceAsStream("secret-read.json");
        String secretReadJson = new BufferedReader(new InputStreamReader(secretReadInputStream)).lines().collect(Collectors.joining());

        Secret secret = objectMapper.readValue(secretReadJson, Secret.class);
        when(secretCreateApi.secret(yaml)).thenReturn(secret);

        String json = objectMapper.writeValueAsString(created(secret).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.TEXT_PLAIN_VALUE).content(yaml))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void secretDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{secret}"), "default", "bootstrap-token-5emitj"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(secretDeleteApi, times(1)).delete("default", "bootstrap-token-5emitj");
    }

    @Test
    public void secretRead() throws Exception {
        when(secretReadApi.read("default", "bootstrap-token-5emitj"))
                .thenReturn(secret());

        InputStream inputStream = getClass().getResourceAsStream("secret-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Secret value = objectMapper.readValue(json, Secret.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{secret}"), "default", "bootstrap-token-5emitj"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public Secret secret() {
        SecretBuilder builder = new SecretBuilder();
        Secret secret = builder.withImmutable(true)
                .withMetadata(new ObjectMetaBuilder().withName("bootstrap-token-5emitj")
                        .withNamespace("default")
                        .build())
                .withData(Map.of("auth-extra-groups", "c3lzdGVtOmJvb3RzdHJhcHBlcnM6a3ViZWFkbTpkZWZhdWx0LW5vZGUtdG9rZW4=",
                        "expiration", "MjAyMC0wOS0xM1QwNDozOToxMFo=",
                        "token-id", "NWVtaXRq",
                        "token-secret", "a3E0Z2lodnN6emduMXAwcg==",
                        "usage-bootstrap-authentication", "dHJ1ZQ==",
                        "usage-bootstrap-signing", "dHJ1ZQ=="))
                .withApiVersion("v1")
                .withKind("Secret")
                .withType("bootstrap.kubernetes.io/token")
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


        String body = objectMapper.writeValueAsString(Map.of());
        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default").contentType(MediaType.APPLICATION_JSON)
                .content(body))
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
