package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.*;
import io.hotcloud.kubernetes.api.workload.CronJobApi;
import io.hotcloud.kubernetes.model.YamlBody;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static io.hotcloud.common.WebResponse.created;
import static io.hotcloud.common.WebResponse.ok;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = CronJobController.class)
@MockBeans(value = {
        @MockBean(classes = {
                CronJobApi.class
        })
})
public class CronJobControllerTest {

    public final static String PATH = "/v1/kubernetes/cronjobs";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CronJobApi cronJobApi;

    @Test
    public void cronjobDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{cronjob}"), "default", "hello"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(cronJobApi, times(1)).delete("default", "hello");
    }

    @Test
    public void cronjobCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("cronjob-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream cronjobReadInputStream = getClass().getResourceAsStream("cronjob-read.json");
        String cronjobReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(cronjobReadInputStream))).lines().collect(Collectors.joining());

        CronJob cronjob = objectMapper.readValue(cronjobReadJson, CronJob.class);
        when(cronJobApi.cronjob(yaml)).thenReturn(cronjob);

        String json = objectMapper.writeValueAsString(created(cronjob).getBody());

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void cronjobRead() throws Exception {
        when(cronJobApi.read("default", "hello")).thenReturn(cronjob());

        InputStream inputStream = getClass().getResourceAsStream("cronjob-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        Job value = objectMapper.readValue(json, Job.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{cronjob}"), "default", "hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void cronjobListRead() throws Exception {
        when(cronJobApi.read("default", Map.of())).thenReturn(cronjobList());

        InputStream inputStream = getClass().getResourceAsStream("cronjobList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        JobList value = objectMapper.readValue(json, JobList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public CronJobList cronjobList() {

        CronJobListBuilder cronjobListBuilder = new CronJobListBuilder();

        return cronjobListBuilder.withApiVersion("batch/v1")
                .withKind("CronJobList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("18881").build())
                .withItems(cronjob())
                .build();
    }

    public CronJob cronjob() {
        CronJobBuilder cronjobBuilder = new CronJobBuilder();

        return cronjobBuilder.withApiVersion("batch/v1")
                .withKind("CronJob")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("hello")
                        .withNamespace("default")
                        .build())
                .withSpec(new CronJobSpecBuilder()
                        .withSchedule("*/1 * * * *")
                        .withSuccessfulJobsHistoryLimit(3)
                        .withSuspend(false)
                        .withConcurrencyPolicy("Allow")
                        .withFailedJobsHistoryLimit(1)
                        .withJobTemplate(
                                new JobTemplateSpecBuilder().withSpec(
                                        new JobSpecBuilder().withTemplate(
                                                new PodTemplateSpecBuilder()
                                                        .withSpec(
                                                                new PodSpecBuilder().withContainers(
                                                                        new ContainerBuilder().withCommand(
                                                                                "/bin/sh",
                                                                                "-c",
                                                                                "date; echo Hello from the Kubernetes cluster"
                                                                        )
                                                                                .withImage("busybox")
                                                                                .withImagePullPolicy("IfNotPresent")
                                                                                .withName("hello")
                                                                                .build()
                                                                ).build()
                                                        ).build()
                                        ).build()
                                ).build()
                        )
                        .build()

                )
                .build();
    }


}
