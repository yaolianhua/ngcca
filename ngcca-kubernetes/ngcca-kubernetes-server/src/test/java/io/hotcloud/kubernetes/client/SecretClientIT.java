package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.SecretClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@EnableKubernetesAgentClient
public class SecretClientIT extends ClientIntegrationTestBase {

    private static final String SECRET = "jason-secret";
    private static final String NAMESPACE = "default";

    @Autowired
    private SecretClient secretClient;

    @Before
    public void init() throws ApiException {
        create();
    }

    @After
    public void post() throws ApiException {
        secretClient.delete(NAMESPACE, SECRET);
        printNamespacedEvents(NAMESPACE, SECRET);
    }

    @Test
    public void read() {
        SecretList readList = secretClient.readList(NAMESPACE, null);
        List<Secret> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .toList();
        names.forEach(System.out::println);

        Secret result = secretClient.read(NAMESPACE, SECRET);
        String name = result.getMetadata().getName();
        Assert.assertEquals(SECRET, name);

    }

    void create() throws ApiException {

        SecretCreateRequest createRequest = new SecretCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(SECRET);
        createRequest.setMetadata(objectMetadata);
        createRequest.setStringData(Map.of());

        secretClient.create(createRequest);
    }

}
