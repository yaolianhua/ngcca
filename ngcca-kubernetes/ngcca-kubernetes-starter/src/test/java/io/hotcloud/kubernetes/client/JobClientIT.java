package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.JobClient;
import io.hotcloud.kubernetes.model.YamlBody;
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
public class JobClientIT extends ClientIntegrationTestBase {

    private static final String JOB = "jason-pi";
    private static final String NAMESPACE = "default";
    @Autowired
    private JobClient jobClient;

    @Before
    public void init() throws ApiException {
        log.info("Job Client Integration Test Start");
        create();
        log.info("Create Job: {}", JOB);
    }

    @After
    public void post() throws ApiException {
        jobClient.delete(NAMESPACE, JOB);
        log.info("Delete Job: {}", JOB);
        log.info("Job Client Integration Test End");
    }

    @Test
    public void read() throws InterruptedException {
        JobList readList = jobClient.readList(NAMESPACE, null);
        List<Job> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Job: {}", names);

        Job result = jobClient.read(NAMESPACE, JOB);
        String name = result.getMetadata().getName();
        Assert.assertEquals(JOB, name);

        waitPodRunningThenFetchContainerLogs(NAMESPACE, JOB, "perl:5.34.0");
    }

    /**
     * <pre>{@code apiVersion: batch/v1
     *
     * kind: Job
     * metadata:
     *   name: pi
     * spec:
     *   template:
     *     spec:
     *       containers:
     *       - name: pi
     *         image: perl:5.34.0
     *         command: ["perl",  "-Mbignum=bpi", "-wle", "print bpi(2000)"]
     *       restartPolicy: Never
     *   backoffLimit: 4}<pre/>
     */
    void create() throws ApiException {
        String stringifyYaml = "{apiVersion: batch/v1, kind: Job, metadata: {name: jason-pi}, spec: {template: {spec: {containers: [{name: pi, image: 'perl:5.34.0', command: [perl, '-Mbignum=bpi', '-wle', 'print bpi(2000)']}], restartPolicy: Never}}, backoffLimit: 4}}";
        jobClient.create(YamlBody.of(stringifyYaml));
    }

}
