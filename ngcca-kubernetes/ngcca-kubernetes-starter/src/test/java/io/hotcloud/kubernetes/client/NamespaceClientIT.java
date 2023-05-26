package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@EnableKubernetesAgentClient
public class NamespaceClientIT extends ClientIntegrationTestBase {

    private static final String NAMESPACE = "jason";

    @Autowired
    private NamespaceClient namespaceClient;

    @Before
    public void before() throws ApiException {
        namespaceClient.create(NAMESPACE);
    }

    @Test
    public void read() {
        NamespaceList readList = namespaceClient.readList(null);
        List<Namespace> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .toList();
        names.forEach(System.out::println);

        Namespace result = namespaceClient.read(NAMESPACE);
        String name = result.getMetadata().getName();
        Assert.assertEquals(NAMESPACE, name);

    }

    @After
    public void post() throws ApiException {
        namespaceClient.delete(NAMESPACE);
        printNamespacedEvents(NAMESPACE, NAMESPACE);
    }

}
