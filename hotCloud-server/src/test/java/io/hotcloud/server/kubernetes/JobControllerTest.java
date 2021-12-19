package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.*;
import io.hotcloud.core.kubernetes.YamlBody;
import io.hotcloud.core.kubernetes.workload.JobCreateApi;
import io.hotcloud.core.kubernetes.workload.JobDeleteApi;
import io.hotcloud.core.kubernetes.workload.JobReadApi;
import io.hotcloud.server.kubernetes.controller.JobController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static io.hotcloud.server.WebResponse.created;
import static io.hotcloud.server.WebResponse.ok;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = JobController.class)
@MockBeans(value = {
        @MockBean(classes = {
                JobCreateApi.class,
                JobReadApi.class,
                JobDeleteApi.class
        })
})
public class JobControllerTest {

    public final static String PATH = "/v1/kubernetes/jobs";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JobCreateApi jobCreateApi;
    @MockBean
    private JobReadApi jobReadApi;
    @MockBean
    private JobDeleteApi jobDeleteApi;

    @Test
    public void jobDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{job}"), "default", "kaniko"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(jobDeleteApi, times(1)).delete("default", "kaniko");
    }

    @Test
    public void jobCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("job-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));

        InputStream jobReadInputStream = getClass().getResourceAsStream("job-read.json");
        String jobReadJson = new BufferedReader(new InputStreamReader(jobReadInputStream)).lines().collect(Collectors.joining());

        Job job = objectMapper.readValue(jobReadJson, Job.class);
        when(jobCreateApi.job(yaml)).thenReturn(job);

        String json = objectMapper.writeValueAsString(created(job).getBody());

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void jobRead() throws Exception {
        when(jobReadApi.read("default", "kaniko")).thenReturn(job());

        InputStream inputStream = getClass().getResourceAsStream("job-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Job value = objectMapper.readValue(json, Job.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{job}"), "default", "kaniko"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void jobListRead() throws Exception {
        when(jobReadApi.read("default", Map.of())).thenReturn(jobList());

        InputStream inputStream = getClass().getResourceAsStream("jobList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        JobList value = objectMapper.readValue(json, JobList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public JobList jobList() {

        JobListBuilder jobListBuilder = new JobListBuilder();
        JobList jobList = jobListBuilder.withApiVersion("batch/v1")
                .withKind("JobList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("17674").build())
                .withItems(job())
                .build();

        return jobList;
    }

    public Job job() {
        JobBuilder jobBuilder = new JobBuilder();

        Job job = jobBuilder.withApiVersion("batch/v1")
                .withKind("Job")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("kaniko")
                        .withNamespace("default")
                        .withLabels(Map.of("job-name", "kaniko"))
                        .build())
                .withSpec(new JobSpecBuilder()
                        .withActiveDeadlineSeconds(1800L)
                        .withBackoffLimit(3)
                        .withTtlSecondsAfterFinished(600)
                        .withTemplate(
                                new PodTemplateSpecBuilder()
                                        .withMetadata(new ObjectMetaBuilder().withLabels(Map.of("job-name", "kaniko")).build())
                                        .withSpec(
                                                new PodSpecBuilder()
                                                        .withContainers(
                                                                new ContainerBuilder()
                                                                        .withArgs("--dockerfile=/workspace/Dockerfile",
                                                                                "--verbosity=debug",
                                                                                "--context=dir://workspace",
                                                                                "--insecure=true",
                                                                                "--insecure-pull=true",
                                                                                "--insecure-registry=harbor.cloud2go.cn",
                                                                                "--destination=harbor.cloud2go.cn/cloudtogo/ubuntu:1.0")
                                                                        .withImage("gcr.io/kaniko-project/executor:latest")
                                                                        .withImagePullPolicy("IfNotPresent")
                                                                        .withName("kaniko")
                                                                        .withVolumeMounts(
                                                                                new VolumeMountBuilder().withMountPath("/kaniko/.docker").withName("kaniko-secret").build(),
                                                                                new VolumeMountBuilder().withMountPath("/workspace").withName("dockerfile-storage").build()
                                                                        )
                                                                        .build()
                                                        )
                                                        .withDnsPolicy("ClusterFirst")
                                                        .withRestartPolicy("Never")
                                                        .withVolumes(
                                                                new VolumeBuilder()
                                                                        .withSecret(new SecretVolumeSourceBuilder().withDefaultMode(420).withSecretName("regcred").withItems(new KeyToPathBuilder().withKey(".dockerconfigjson").withPath("config.json").build())
                                                                                .build())
                                                                        .withName("kaniko-secret")
                                                                        .build(),
                                                                new VolumeBuilder()
                                                                        .withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder().withClaimName("dockerfile-claim").build())
                                                                        .withName("dockerfile-storage")
                                                                        .build()
                                                        ).build()
                                        ).build()
                        ).build()

                )
                .build();

        return job;
    }


}
