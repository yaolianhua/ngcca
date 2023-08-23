package io.hotcloud.kubernetes.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.PersistentVolumeApi;
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
@WebMvcTest(value = PersistentVolumeController.class)
@MockBeans(value = {
        @MockBean(classes = {
                PersistentVolumeApi.class
        })
})
public class PersistentVolumeControllerTest {

    public final static String PATH = "/v1/kubernetes/persistentvolumes";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PersistentVolumeApi persistentVolumeApi;

    @Test
    public void persistentvolumeDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{persistentvolume}"), "dockerfile"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(persistentVolumeApi, times(1)).delete("dockerfile");
    }

    @Test
    public void persistentvolumeCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("persistentVolume-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream persistentvolumeReadInputStream = getClass().getResourceAsStream("persistentVolume-read.json");
        String persistentvolumeReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(persistentvolumeReadInputStream)))
                .lines()
                .collect(Collectors.joining());

        PersistentVolume persistentVolume = objectMapper.readValue(persistentvolumeReadJson, PersistentVolume.class);
        when(persistentVolumeApi.create(yaml)).thenReturn(persistentVolume);

        String json = objectMapper.writeValueAsString(persistentVolume);

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void persistentVolumeRead() throws Exception {
        when(persistentVolumeApi.read("dockerfile")).thenReturn(persistentvolume());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolume-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        PersistentVolume value = objectMapper.readValue(json, PersistentVolume.class);
        String _json = objectMapper.writeValueAsString(value);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{persistentvolume}"), "dockerfile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void persistentvolumeListRead() throws Exception {
        when(persistentVolumeApi.read(Map.of())).thenReturn(persistentvolumeList());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolumeList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        PersistentVolumeList value = objectMapper.readValue(json, PersistentVolumeList.class);
        String _json = objectMapper.writeValueAsString(value);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public PersistentVolumeList persistentvolumeList() {

        PersistentVolumeListBuilder persistentVolumeListBuilder = new PersistentVolumeListBuilder();

        return persistentVolumeListBuilder.withApiVersion("v1")
                .withKind("PersistentVolumeList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("219596").build())
                .withItems(persistentvolume())
                .build();
    }

    public PersistentVolume persistentvolume() {
        PersistentVolumeBuilder persistentVolumeBuilder = new PersistentVolumeBuilder();

        return persistentVolumeBuilder.withApiVersion("v1")
                .withKind("PersistentVolume")
                .withMetadata(new ObjectMetaBuilder().withName("dockerfile").build())
                .withSpec(new PersistentVolumeSpecBuilder()
                        .withAccessModes("ReadWriteOnce")
                        .withCapacity(Map.of("storage", Quantity.parse("1Gi")))
                        .withClaimRef(new ObjectReferenceBuilder().withApiVersion("v1").withKind("PersistentVolumeClaim").withName("dockerfile-claim").withNamespace("default").build())
                        .withHostPath(new HostPathVolumeSourceBuilder().withPath("/kaniko").withType("").build())
                        .withPersistentVolumeReclaimPolicy("Retain")
                        .withVolumeMode("Filesystem")
                        .build())
                .build();
    }


}
