package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.namespace.NamespaceHttpClient;
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
@EnableHotCloudHttpClient
public class NamespaceHttpClientIT extends ClientIntegrationTestBase {

    private static final String NAMESPACE = "namespace-test";

    @Autowired
    private NamespaceHttpClient namespaceHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Namespace Client Integration Test Start");
        namespaceHttpClient.create(NAMESPACE);
        log.info("Create Namespace Name: '{}'", NAMESPACE);
    }

    @Test
    public void read() {
        NamespaceList readList = namespaceHttpClient.readList(null);
        List<Namespace> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Namespace Name: {}", names);

        Namespace result = namespaceHttpClient.read(NAMESPACE);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, NAMESPACE);

    }

    @After
    public void post() throws ApiException {
        namespaceHttpClient.delete(NAMESPACE);
        log.info("Delete Namespace Name: '{}'", NAMESPACE);
        log.info("Namespace Client Integration Test End");
    }

}
