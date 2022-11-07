package io.hotcloud.kubernetes.client;


import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.storage.StorageClassCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class StorageClassHttpClientIT extends ClientIntegrationTestBase {

    private static final String STORAGE_CLASS = "local-storage";

    @Autowired
    private StorageClassHttpClient storageClassHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("StorageClass Client Integration Test Start");
        create();
        log.info("Create StorageClass Name: '{}'", STORAGE_CLASS);
    }

    @After
    public void post() throws ApiException {
        storageClassHttpClient.delete(STORAGE_CLASS);
        log.info("Delete StorageClass Name: '{}'", STORAGE_CLASS);
        log.info("StorageClass Client Integration Test End");
    }

    @Test
    public void read() {
        StorageClassList readList = storageClassHttpClient.readList(null);
        List<StorageClass> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List StorageClass Name: {}", names);

        StorageClass result = storageClassHttpClient.read(STORAGE_CLASS);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, STORAGE_CLASS);

    }

    void create() throws ApiException {

        StorageClassCreateRequest createRequest = new StorageClassCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(STORAGE_CLASS);

        createRequest.setMetadata(objectMetadata);

        storageClassHttpClient.create(createRequest);
    }

}
