package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.batch.v1.CronJob;
import io.fabric8.kubernetes.api.model.batch.v1.CronJobList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.CronJobClient;
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
import java.util.stream.Collectors;

@Slf4j
@EnableKubernetesAgentClient
public class CronJobClientIT extends ClientIntegrationTestBase {

    private static final String CRONJOB = "jason-cronjob";
    private static final String NAMESPACE = "default";
    @Autowired
    private CronJobClient cronJobClient;

    @Before
    public void init() throws ApiException {
        //
        create();
    }

    @After
    public void post() throws ApiException {
        cronJobClient.delete(NAMESPACE, CRONJOB);
        //
        printNamespacedEvents(NAMESPACE, CRONJOB);
    }

    @Test
    public void read() throws InterruptedException {
        CronJobList readList = cronJobClient.readList(NAMESPACE, null);
        List<CronJob> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List CronJob: {}", names);

        CronJob result = cronJobClient.read(NAMESPACE, CRONJOB);
        String name = result.getMetadata().getName();
        Assert.assertEquals(CRONJOB, name);

        waitPodRunningThenFetchContainerLogs(NAMESPACE, CRONJOB, "busybox");
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

        cronJobClient.create(request);

    }

}
