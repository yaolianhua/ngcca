package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimCreateApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimDeleteApi;
import io.hotcloud.kubernetes.api.volume.PersistentVolumeClaimReadApi;
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
@WebMvcTest(value = PersistentVolumeClaimController.class)
@MockBeans(value = {
        @MockBean(classes = {
                PersistentVolumeClaimReadApi.class,
                PersistentVolumeClaimCreateApi.class,
                PersistentVolumeClaimDeleteApi.class
        })
})
public class PersistentVolumeClaimControllerTest {

    public final static String PATH = "/v1/kubernetes/persistentvolumeclaims";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PersistentVolumeClaimReadApi persistentVolumeClaimReadApi;
    @MockBean
    private PersistentVolumeClaimCreateApi persistentVolumeClaimCreateApi;
    @MockBean
    private PersistentVolumeClaimDeleteApi persistentVolumeClaimDeleteApi;

    @Test
    public void persistentvolumeclaimDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{persistentvolumeclaims}"), "default", "dockerfile-claim"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(persistentVolumeClaimDeleteApi, times(1)).delete("dockerfile-claim", "default");
    }

    @Test
    public void persistentvolumeclaimCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("persistentVolumeClaim-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        InputStream persistentvolumeclaimReadInputStream = getClass().getResourceAsStream("persistentVolumeClaim-read.json");
        String persistentvolumeclaimReadJson = new BufferedReader(new InputStreamReader(persistentvolumeclaimReadInputStream))
                .lines()
                .collect(Collectors.joining());

        PersistentVolumeClaim persistentVolumeClaim = objectMapper.readValue(persistentvolumeclaimReadJson, PersistentVolumeClaim.class);
        when(persistentVolumeClaimCreateApi.persistentVolumeClaim(yaml)).thenReturn(persistentVolumeClaim);

        String json = objectMapper.writeValueAsString(created(persistentVolumeClaim).getBody());

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void persistentVolumeClaimRead() throws Exception {
        when(persistentVolumeClaimReadApi.read("default", "dockerfile-claim")).thenReturn(persistentvolumeclaim());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolumeClaim-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        PersistentVolume value = objectMapper.readValue(json, PersistentVolume.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{persistentvolumeclaims}"), "default", "dockerfile-claim"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void persistentvolumeclaimListRead() throws Exception {
        when(persistentVolumeClaimReadApi.read("default", Map.of())).thenReturn(persistentvolumeclaimList());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolumeClaimList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        PersistentVolumeClaimList value = objectMapper.readValue(json, PersistentVolumeClaimList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public PersistentVolumeClaimList persistentvolumeclaimList() {

        PersistentVolumeClaimListBuilder persistentVolumeClaimListBuilder = new PersistentVolumeClaimListBuilder();
        PersistentVolumeClaimList persistentVolumeClaimList = persistentVolumeClaimListBuilder.withApiVersion("v1")
                .withKind("PersistentVolumeClaimList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("219844").build())
                .withItems(persistentvolumeclaim())
                .build();

        return persistentVolumeClaimList;
    }

    public PersistentVolumeClaim persistentvolumeclaim() {
        PersistentVolumeClaimBuilder persistentVolumeClaimBuilder = new PersistentVolumeClaimBuilder();

        PersistentVolumeClaim persistentVolumeCalim = persistentVolumeClaimBuilder.withApiVersion("v1")
                .withKind("PersistentVolumeClaim")
                .withMetadata(new ObjectMetaBuilder().withName("dockerfile-claim").withNamespace("default").build())
                .withSpec(new PersistentVolumeClaimSpecBuilder()
                        .withAccessModes("ReadWriteOnce")
                        .withResources(new ResourceRequirementsBuilder().withRequests(Map.of("storage", Quantity.parse("1Gi"))).build())
                        .withStorageClassName("")
                        .withVolumeMode("Filesystem")
                        .withVolumeName("dockerfile")
                        .build())
                .build();

        return persistentVolumeCalim;
    }


}
