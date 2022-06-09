package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.configurations.SecretHttpClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Result;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
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
@EnableHotCloudHttpClient
public class SecretHttpClientIT extends ClientIntegrationTestBase {

    private static final String SECRET = "mysecret";
    private static final String NAMESPACE = "default";

    @Autowired
    private SecretHttpClient secretHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Secret Client Integration Test Start");
        create();
        log.info("Create Secret Name: '{}'", SECRET);
    }

    @After
    public void post() throws ApiException {
        secretHttpClient.delete(NAMESPACE, SECRET);
        log.info("Delete Secret Name: '{}'", SECRET);
        log.info("Secret Client Integration Test End");
    }

    @Test
    public void read() {
        Result<SecretList> readList = secretHttpClient.readList(NAMESPACE, null);
        List<Secret> items = readList.getData().getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Secret Name: {}", names);

        Result<Secret> result = secretHttpClient.read(NAMESPACE, SECRET);
        String name = result.getData().getMetadata().getName();
        Assert.assertEquals(name, SECRET);

    }

    void create() throws ApiException {

        SecretCreateRequest createRequest = new SecretCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(SECRET);
        createRequest.setMetadata(objectMetadata);
        createRequest.setStringData(Map.of());

        secretHttpClient.create(createRequest);
    }

}
