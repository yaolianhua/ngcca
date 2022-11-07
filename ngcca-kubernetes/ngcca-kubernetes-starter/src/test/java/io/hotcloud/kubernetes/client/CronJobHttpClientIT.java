package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.ImagePullPolicy;
import io.hotcloud.kubernetes.model.workload.*;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@EnableKubernetesAgentClient
public class CronJobHttpClientIT extends ClientIntegrationTestBase {

    private static final String CRONJOB = "hello";
    private static final String NAMESPACE = "default";
    @Autowired
    private CronJobHttpClient cronJobHttpClient;
    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("CronJob Client Integration Test Start");
        create();
        log.info("Create CronJob Name: '{}'", CRONJOB);
    }

    @After
    public void post() throws ApiException {
        cronJobHttpClient.delete(NAMESPACE, CRONJOB);
        log.info("Delete CronJob Name: '{}'", CRONJOB);
        log.info("CronJob Client Integration Test End");
    }

    @Test
    public void read() throws InterruptedException {
        CronJobList readList = cronJobHttpClient.readList(NAMESPACE, null);
        List<CronJob> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List CronJob Name: {}", names);

        CronJob result = cronJobHttpClient.read(NAMESPACE, CRONJOB);
        String name = result.getMetadata().getName();
        Assert.assertEquals(name, CRONJOB);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        PodList podListResult = podHttpClient.readList(NAMESPACE, null);
        List<Pod> pods = podListResult.getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .filter(e -> e.startsWith(CRONJOB))
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);

        for (String podName : podNames) {
            String logResult = podHttpClient.logs(NAMESPACE, podName, 100);
            log.info("Fetch Pod [{}] logs \n {}", podName, logResult);
        }


    }

    void create() throws ApiException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(CRONJOB);

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("busybox");
        container.setName("hello");
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        container.setCommand(List.of("/bin/sh", "-c", "date; echo Hello from the Kubernetes cluster"));
        spec.setContainers(List.of(container));
        spec.setRestartPolicy(PodTemplateSpec.RestartPolicy.OnFailure);


        JobTemplate jobTemplate = new JobTemplate();
        jobTemplate.setSpec(spec);

        JobSpec jobSpec = new JobSpec();
        jobSpec.setTemplate(jobTemplate);

        CronJobTemplate template = new CronJobTemplate();
        template.setSpec(jobSpec);

        CronJobSpec cronJobSpec = new CronJobSpec();
        cronJobSpec.setJobTemplate(template);
        cronJobSpec.setSchedule("*/1 * * * *");

        CronJobCreateRequest request = new CronJobCreateRequest();
        request.setSpec(cronJobSpec);
        request.setMetadata(metadata);

        cronJobHttpClient.create(request);

    }

}
