package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.model.ConfigMapCreateRequest;
import io.hotcloud.kubernetes.model.ObjectMetadata;
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
public class ConfigMapHttpClientIT extends ClientIntegrationTestBase {

    private static final String CONFIGMAP = "myconfig";
    private static final String NAMESPACE = "default";

    @Autowired
    private ConfigMapHttpClient configMapHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("ConfigMap Client Integration Test Start");
        create();
        log.info("Create ConfigMap Name: '{}'", CONFIGMAP);
    }

    @After
    public void post() throws ApiException {
        configMapHttpClient.delete(NAMESPACE, CONFIGMAP);
        log.info("Delete ConfigMap Name: '{}'", CONFIGMAP);
        log.info("ConfigMap Client Integration Test End");
    }

    @Test
    public void read() {
        ConfigMapList readList = configMapHttpClient.readList(NAMESPACE, null);
        List<ConfigMap> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List ConfigMap Name: {}", names);

        ConfigMap result = configMapHttpClient.read(NAMESPACE, CONFIGMAP);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, CONFIGMAP);

    }

    void create() throws ApiException {

        ConfigMapCreateRequest createRequest = new ConfigMapCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(CONFIGMAP);
        createRequest.setMetadata(objectMetadata);
        createRequest.setData(Map.of());


        configMapHttpClient.create(createRequest);
    }

}
