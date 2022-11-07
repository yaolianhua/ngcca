package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.HostPathVolume;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeCreateRequest;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeSpec;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class PersistentVolumeClientIT extends ClientIntegrationTestBase {

    private static final String PERSISTENT_VOLUME = "pv0003";

    @Autowired
    private PersistentVolumeClient persistentVolumeClient;

    @Before
    public void init() throws ApiException {
        log.info("PersistentVolume Client Integration Test Start");
        create();
        log.info("Create PersistentVolume Name: '{}'", PERSISTENT_VOLUME);
    }

    @After
    public void post() throws ApiException {
        persistentVolumeClient.delete(PERSISTENT_VOLUME);
        log.info("Delete PersistentVolume Name: '{}'", PERSISTENT_VOLUME);
        log.info("PersistentVolume Client Integration Test End");
    }

    @Test
    public void read() {
        PersistentVolumeList readList = persistentVolumeClient.readList(null);
        List<PersistentVolume> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List PersistentVolume Name: {}", names);

        PersistentVolume result = persistentVolumeClient.read(PERSISTENT_VOLUME);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, PERSISTENT_VOLUME);

    }

    void create() throws ApiException {

        PersistentVolumeCreateRequest createRequest = new PersistentVolumeCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(PERSISTENT_VOLUME);

        PersistentVolumeSpec spec = new PersistentVolumeSpec();
        spec.setAccessModes(List.of("ReadWriteOnce"));
        spec.setCapacity(Map.of("storage", "1Gi"));
        spec.setPersistentVolumeReclaimPolicy(PersistentVolumeSpec.ReclaimPolicy.Recycle);
        HostPathVolume hostPathVolume = new HostPathVolume();
        hostPathVolume.setPath("/tmp");
        hostPathVolume.setType("");
        spec.setHostPath(hostPathVolume);
        spec.setStorageClassName("local");

        createRequest.setSpec(spec);
        createRequest.setMetadata(objectMetadata);
        persistentVolumeClient.create(createRequest);
    }

}
