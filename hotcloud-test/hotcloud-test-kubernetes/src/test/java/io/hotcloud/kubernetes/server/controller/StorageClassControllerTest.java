package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ListMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassBuilder;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.fabric8.kubernetes.api.model.storage.StorageClassListBuilder;
import io.hotcloud.kubernetes.api.storage.StorageClassApi;
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
@WebMvcTest(value = StorageClassController.class)
@MockBeans(value = {
        @MockBean(classes = {
                StorageClassApi.class
        })
})
public class StorageClassControllerTest {

    public final static String PATH = "/v1/kubernetes/storageclasses";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StorageClassApi storageClassApi;

    @Test
    public void storageClassDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{name}"), "local-storage"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(storageClassApi, times(1)).delete("local-storage");
    }

    @Test
    public void storageClassCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("storageClass-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));
        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));

        InputStream resourceAsStream = getClass().getResourceAsStream("storageClass-read.json");
        String readJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)))
                .lines()
                .collect(Collectors.joining());

        StorageClass storageClass = objectMapper.readValue(readJson, StorageClass.class);
        when(storageClassApi.storageClass(yaml)).thenReturn(storageClass);

        String json = objectMapper.writeValueAsString(created(storageClass).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void storageClassRead() throws Exception {
        when(storageClassApi.read("local-storage")).thenReturn(storageclass());

        InputStream inputStream = getClass().getResourceAsStream("storageClass-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        StorageClass value = objectMapper.readValue(json, StorageClass.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{name}"), "local-storage"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void storageClassListRead() throws Exception {
        when(storageClassApi.read(Map.of())).thenReturn(storageClassList());

        InputStream inputStream = getClass().getResourceAsStream("storageClassList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        StorageClassList value = objectMapper.readValue(json, StorageClassList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public StorageClassList storageClassList() {

        StorageClassListBuilder storageClassListBuilder = new StorageClassListBuilder();

        return storageClassListBuilder.withApiVersion("storage.k8s.io/v1")
                .withKind("StorageClassList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("44610").build())
                .withItems(storageclass())
                .build();
    }

    public StorageClass storageclass() {
        StorageClassBuilder storageClassBuilder = new StorageClassBuilder();

        return storageClassBuilder.withApiVersion("storage.k8s.io/v1")
                .withKind("StorageClass")
                .withMetadata(new ObjectMetaBuilder().withName("local-storage").build())
                .withAllowVolumeExpansion(true)
                .withProvisioner("kubernetes.io/no-provisioner")
                .withReclaimPolicy("Delete")
                .withVolumeBindingMode("WaitForFirstConsumer")
                .build();
    }


}
