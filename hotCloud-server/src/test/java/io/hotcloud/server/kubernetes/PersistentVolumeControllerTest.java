package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeCreateApi;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeDeleteApi;
import io.hotcloud.core.kubernetes.volume.PersistentVolumeReadApi;
import io.hotcloud.server.kubernetes.volume.PersistentVolumeController;
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
@WebMvcTest(value = PersistentVolumeController.class)
@MockBeans(value = {
        @MockBean(classes = {
                PersistentVolumeReadApi.class,
                PersistentVolumeCreateApi.class,
                PersistentVolumeDeleteApi.class
        })
})
public class PersistentVolumeControllerTest {

    public final static String PATH = "/v1/kubernetes/persistentvolumes";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PersistentVolumeReadApi persistentVolumeReadApi;
    @MockBean
    private PersistentVolumeCreateApi persistentVolumeCreateApi;
    @MockBean
    private PersistentVolumeDeleteApi persistentVolumeDeleteApi;

    @Test
    public void persistentvolumeDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{persistentvolume}"), "pv0003"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(persistentVolumeDeleteApi, times(1)).delete("pv0003");
    }

    @Test
    public void persistentvolumeCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("persistentVolume-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        InputStream persistentvolumeReadInputStream = getClass().getResourceAsStream("persistentVolume-read.json");
        String persistentvolumeReadJson = new BufferedReader(new InputStreamReader(persistentvolumeReadInputStream))
                .lines()
                .collect(Collectors.joining());

        PersistentVolume persistentVolume = objectMapper.readValue(persistentvolumeReadJson, PersistentVolume.class);
        when(persistentVolumeCreateApi.persistentVolume(yaml)).thenReturn(persistentVolume);

        String json = objectMapper.writeValueAsString(created(persistentVolume).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.TEXT_PLAIN_VALUE).content(yaml))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void persistentVolumeRead() throws Exception {
        when(persistentVolumeReadApi.read("pv0003")).thenReturn(persistentvolume());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolume-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        PersistentVolume value = objectMapper.readValue(json, PersistentVolume.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{persistentvolume}"), "pv0003"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void persistentvolumeListRead() throws Exception {
        when(persistentVolumeReadApi.read(Map.of())).thenReturn(persistentvolumeList());

        InputStream inputStream = getClass().getResourceAsStream("persistentVolumeList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        PersistentVolumeList value = objectMapper.readValue(json, PersistentVolumeList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        String body = objectMapper.writeValueAsString(Map.of());
        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH).contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public PersistentVolumeList persistentvolumeList() {

        PersistentVolumeListBuilder persistentVolumeListBuilder = new PersistentVolumeListBuilder();
        PersistentVolumeList persistentVolumeList = persistentVolumeListBuilder.withApiVersion("v1")
                .withKind("PersistentVolumeList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("219596").build())
                .withItems(persistentvolume())
                .build();

        return persistentVolumeList;
    }

    public PersistentVolume persistentvolume() {
        PersistentVolumeBuilder persistentVolumeBuilder = new PersistentVolumeBuilder();

        PersistentVolume persistentVolume = persistentVolumeBuilder.withApiVersion("v1")
                .withKind("PersistentVolume")
                .withMetadata(new ObjectMetaBuilder().withName("pv0003").build())
                .withSpec(new PersistentVolumeSpecBuilder()
                        .withAccessModes("ReadWriteOnce")
                        .withCapacity(Map.of("storage", Quantity.parse("1Gi")))
                        .withClaimRef(new ObjectReferenceBuilder().withApiVersion("v1").withKind("PersistentVolumeClaim").withName("myclaim").withNamespace("default").build())
                        .withHostPath(new HostPathVolumeSourceBuilder().withPath("/tmp").withType("").build())
                        .withPersistentVolumeReclaimPolicy("Recycle")
                        .withVolumeMode("Filesystem")
                        .build())
                .build();

        return persistentVolume;
    }


}
