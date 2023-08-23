package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.ServiceClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.network.DefaultServiceSpec;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@EnableKubernetesAgentClient
public class ServiceClientIT extends ClientIntegrationTestBase {

    private static final String SERVICE = "jason-service";
    private static final String NAMESPACE = "default";

    @Autowired
    private ServiceClient serviceClient;

    @Before
    public void init() throws ApiException {
        create();
    }

    @After
    public void post() throws ApiException {
        serviceClient.delete(NAMESPACE, SERVICE);
        printNamespacedEvents(NAMESPACE, SERVICE);
    }

    @Test
    public void read() {
        ServiceList readList = serviceClient.readList(NAMESPACE, null);
        List<Service> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName()).toList();
        names.forEach(System.out::println);

        Service result = serviceClient.read(NAMESPACE, SERVICE);
        String name = result.getMetadata().getName();
        Assert.assertEquals(SERVICE, name);

    }

    void create() throws ApiException {

        ServiceCreateRequest createRequest = new ServiceCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(SERVICE);

        ServicePort servicePort = new ServicePort();
        servicePort.setPort(8001);

        DefaultServiceSpec serviceSpec = new DefaultServiceSpec();
        serviceSpec.setPorts(List.of(servicePort));

        createRequest.setServiceMetadata(objectMetadata);
        createRequest.setServiceSpec(serviceSpec);
        serviceClient.create(createRequest);
    }

}
