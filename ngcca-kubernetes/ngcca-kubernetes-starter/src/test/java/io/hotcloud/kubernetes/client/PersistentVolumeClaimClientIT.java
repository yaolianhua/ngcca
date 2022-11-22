package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClaimClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimSpec;
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
public class PersistentVolumeClaimClientIT extends ClientIntegrationTestBase {

    private static final String PERSISTENT_VOLUME_CLAIM = "myclaim";
    private static final String NAMESPACE = "default";

    @Autowired
    private PersistentVolumeClaimClient persistentVolumeClaimClient;

    @Before
    public void init() throws ApiException {
        log.info("PersistentVolumeClaim Client Integration Test Start");
        create();
        log.info("Create PersistentVolumeClaim Name: '{}'", PERSISTENT_VOLUME_CLAIM);
    }

    @After
    public void post() throws ApiException {
        persistentVolumeClaimClient.delete(NAMESPACE, PERSISTENT_VOLUME_CLAIM);
        log.info("Delete PersistentVolumeClaim Name: '{}'", PERSISTENT_VOLUME_CLAIM);
        log.info("PersistentVolumeClaim Client Integration Test End");
    }

    @Test
    public void read() {
        PersistentVolumeClaimList readList = persistentVolumeClaimClient.readList(NAMESPACE, null);
        List<PersistentVolumeClaim> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List PersistentVolumeClaim Name: {}", names);

        PersistentVolumeClaim result = persistentVolumeClaimClient.read(NAMESPACE, PERSISTENT_VOLUME_CLAIM);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, PERSISTENT_VOLUME_CLAIM);

    }

    void create() throws ApiException {

        PersistentVolumeClaimCreateRequest createRequest = new PersistentVolumeClaimCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(PERSISTENT_VOLUME_CLAIM);

        PersistentVolumeClaimSpec claimSpec = new PersistentVolumeClaimSpec();
        claimSpec.setAccessModes(List.of("ReadWriteOnce"));

        Resources resources = new Resources();
        resources.setRequests(Map.of("storage", "1Gi"));

        claimSpec.setResources(resources);

        createRequest.setMetadata(objectMetadata);
        createRequest.setSpec(claimSpec);
        persistentVolumeClaimClient.create(createRequest);
    }

}
