package io.hotcloud.kubernetes.client;


import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.StorageClassClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@EnableKubernetesAgentClient
public class StorageClassClientIT extends ClientIntegrationTestBase {

    private static final String STORAGE_CLASS = "jason-storage";

    @Autowired
    private StorageClassClient storageClassClient;

    @Before
    public void init() throws ApiException {
        create();
    }

    @After
    public void post() throws ApiException {
        storageClassClient.delete(STORAGE_CLASS);
    }

    @Test
    public void read() {
        StorageClassList readList = storageClassClient.readList(null);
        List<StorageClass> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName()).toList();
        names.forEach(System.out::println);

        StorageClass result = storageClassClient.read(STORAGE_CLASS);
        String name = result.getMetadata().getName();
        Assert.assertEquals(STORAGE_CLASS, name);

    }

    void create() throws ApiException {

        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(STORAGE_CLASS);

        createRequest.setMetadata(objectMetadata);

        storageClassClient.create(createRequest);
    }

}
