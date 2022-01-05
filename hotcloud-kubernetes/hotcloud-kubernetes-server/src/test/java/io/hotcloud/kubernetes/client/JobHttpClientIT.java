package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.hotcloud.Result;
import io.hotcloud.kubernetes.IntegrationTestBase;
import io.hotcloud.kubernetes.client.workload.JobHttpClient;
import io.hotcloud.kubernetes.client.workload.PodHttpClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.pod.PodTemplateSpec;
import io.hotcloud.kubernetes.model.pod.container.Container;
import io.hotcloud.kubernetes.model.pod.container.ImagePullPolicy;
import io.hotcloud.kubernetes.model.pod.container.VolumeMount;
import io.hotcloud.kubernetes.model.volume.PersistentVolumeClaimVolume;
import io.hotcloud.kubernetes.model.volume.SecretVolume;
import io.hotcloud.kubernetes.model.volume.Volume;
import io.hotcloud.kubernetes.model.workload.JobCreateRequest;
import io.hotcloud.kubernetes.model.workload.JobSpec;
import io.hotcloud.kubernetes.model.workload.JobTemplate;
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
@EnableHotCloudHttpClient
public class JobHttpClientIT extends IntegrationTestBase {

    private static final String JOB = "kaniko";
    private static final String NAMESPACE = "default";
    @Autowired
    private JobHttpClient jobHttpClient;
    @Autowired
    private PodHttpClient podHttpClient;

    @Before
    public void init() throws ApiException {
        log.info("Job Client Integration Test Start");
        create();
        log.info("Create Job Name: '{}'", JOB);
    }

    @After
    public void post() throws ApiException {
        jobHttpClient.delete(NAMESPACE, JOB);
        log.info("Delete Job Name: '{}'", JOB);
        log.info("Job Client Integration Test End");
    }

    @Test
    public void read() throws InterruptedException {
        Result<JobList> readList = jobHttpClient.readList(NAMESPACE, null);
        List<Job> items = readList.getData().getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());
        log.info("List Job Name: {}", names);

        Result<Job> result = jobHttpClient.read(NAMESPACE, JOB);
        String name = result.getData().getMetadata().getName();
        Assert.assertEquals(name, JOB);

        log.info("Sleep 30s wait pod created");
        TimeUnit.SECONDS.sleep(30);
        Result<PodList> podListResult = podHttpClient.readList(NAMESPACE, null);
        List<Pod> pods = podListResult.getData().getItems();
        List<String> podNames = pods.stream()
                .map(e -> e.getMetadata().getName())
                .filter(e -> e.startsWith(JOB))
                .collect(Collectors.toList());
        log.info("List Pod Name: {}", podNames);

        for (String podName : podNames) {
            Result<String> logResult = podHttpClient.logs(NAMESPACE, podName, 100);
            log.info("Fetch Pod [{}] logs \n {}", podName, logResult.getData());
        }
    }

    void create() throws ApiException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(JOB);

        JobSpec jobSpec = new JobSpec();

        PodTemplateSpec spec = new PodTemplateSpec();
        Container container = new Container();
        container.setImage("gcr.io/kaniko-project/executor:latest");
        container.setName("kaniko");
        container.setImagePullPolicy(ImagePullPolicy.IfNotPresent);

        container.setArgs(List.of("--dockerfile=/workspace/Dockerfile",
                "--verbosity=debug",
                "--context=dir://workspace",
                "--insecure=true",
                "--insecure-pull=true",
                "--insecure-registry=harbor.cloud2go.cn",
                "--destination=harbor.cloud2go.cn/cloudtogo/ubuntu:1.0"));

        VolumeMount secret = new VolumeMount();
        secret.setName("kaniko-secret");
        secret.setMountPath("/kaniko/.docker");

        VolumeMount dockerfile = new VolumeMount();
        dockerfile.setName("dockerfile-storage");
        dockerfile.setMountPath("/workspace");

        container.setVolumeMounts(List.of(secret, dockerfile));

        spec.setContainers(List.of(container));

        Volume v1 = new Volume();
        SecretVolume secretVolume = new SecretVolume();
        secretVolume.setSecretName("regcred");
        SecretVolume.Item item = new SecretVolume.Item();
        item.setKey(".dockerconfigjson");
        item.setPath("config.json");
        secretVolume.setItems(List.of(item));

        v1.setName("kaniko-secret");
        v1.setSecretVolume(secretVolume);

        Volume v2 = new Volume();
        v2.setName("dockerfile-storage");
        PersistentVolumeClaimVolume claimVolume = new PersistentVolumeClaimVolume();
        claimVolume.setClaimName("dockerfile-claim");
        v2.setPersistentVolumeClaim(claimVolume);

        spec.setVolumes(List.of(v1, v2));

        JobTemplate template = new JobTemplate();
        template.setSpec(spec);

        jobSpec.setTemplate(template);
        jobSpec.setTtlSecondsAfterFinished(600);
        jobSpec.setBackoffLimit(3);
        jobSpec.setActiveDeadlineSeconds(1800L);

        JobCreateRequest request = new JobCreateRequest();
        request.setSpec(jobSpec);
        request.setMetadata(metadata);

        jobHttpClient.create(request);

    }

}
