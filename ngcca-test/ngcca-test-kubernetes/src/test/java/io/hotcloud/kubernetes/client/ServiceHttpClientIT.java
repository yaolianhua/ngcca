package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.network.ServiceHttpClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Result;
import io.hotcloud.kubernetes.model.network.DefaultServiceSpec;
import io.hotcloud.kubernetes.model.network.ServiceCreateRequest;
import io.hotcloud.kubernetes.model.network.ServicePort;
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
public class ServiceHttpClientIT extends ClientIntegrationTestBase {

    private static final String SERVICE = "myservice";
    private static final String NAMESPACE = "default";

    @Autowired
    private ServiceHttpClient serviceHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Service Client Integration Test Start");
        create();
        log.info("Create Service Name: '{}'", SERVICE);
    }

    @After
    public void post() throws ApiException {
        serviceHttpClient.delete(NAMESPACE, SERVICE);
        log.info("Delete Service Name: '{}'", SERVICE);
        log.info("Service Client Integration Test End");
    }

    @Test
    public void read() {
        Result<ServiceList> readList = serviceHttpClient.readList(NAMESPACE, null);
        List<Service> items = readList.getData().getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Service Name: {}", names);

        Result<Service> result = serviceHttpClient.read(NAMESPACE, SERVICE);
        String name = result.getData().getMetadata().getName();
        Assert.assertEquals(name, SERVICE);

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
        serviceHttpClient.create(createRequest);
    }

}
