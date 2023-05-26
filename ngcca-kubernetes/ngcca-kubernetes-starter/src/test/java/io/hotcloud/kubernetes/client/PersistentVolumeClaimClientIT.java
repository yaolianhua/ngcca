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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


@EnableKubernetesAgentClient
public class PersistentVolumeClaimClientIT extends ClientIntegrationTestBase {

    private static final String PERSISTENT_VOLUME_CLAIM = "jason-claim";
    private static final String NAMESPACE = "default";

    @Autowired
    private PersistentVolumeClaimClient persistentVolumeClaimClient;

    @Before
    public void init() throws ApiException {
        create();
    }

    @After
    public void post() throws ApiException {
        persistentVolumeClaimClient.delete(NAMESPACE, PERSISTENT_VOLUME_CLAIM);
        printNamespacedEvents(NAMESPACE, PERSISTENT_VOLUME_CLAIM);
    }

    @Test
    public void read() {
        PersistentVolumeClaimList readList = persistentVolumeClaimClient.readList(NAMESPACE, null);
        List<PersistentVolumeClaim> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName()).toList();
        names.forEach(System.out::println);

        PersistentVolumeClaim result = persistentVolumeClaimClient.read(NAMESPACE, PERSISTENT_VOLUME_CLAIM);
        String name = result.getMetadata().getName();
        Assert.assertEquals(PERSISTENT_VOLUME_CLAIM, name);

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
