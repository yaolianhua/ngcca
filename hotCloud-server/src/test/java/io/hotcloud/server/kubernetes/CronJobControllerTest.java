package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.*;
import io.hotcloud.core.kubernetes.workload.CronJobCreateApi;
import io.hotcloud.core.kubernetes.workload.CronJobDeleteApi;
import io.hotcloud.core.kubernetes.workload.CronJobReadApi;
import io.hotcloud.server.kubernetes.workload.CronJobController;
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
@WebMvcTest(value = CronJobController.class)
@MockBeans(value = {
        @MockBean(classes = {
                CronJobCreateApi.class,
                CronJobReadApi.class,
                CronJobDeleteApi.class
        })
})
public class CronJobControllerTest {

    public final static String PATH = "/v1/kubernetes/cronjobs";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CronJobCreateApi cronJobCreateApi;
    @MockBean
    private CronJobReadApi cronJobReadApi;
    @MockBean
    private CronJobDeleteApi cronJobDeleteApi;

    @Test
    public void cronjobDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{cronjob}"), "default", "hello"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(cronJobDeleteApi, times(1)).delete("default", "hello");
    }

    @Test
    public void cronjobCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("cronjob-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        InputStream cronjobReadInputStream = getClass().getResourceAsStream("cronjob-read.json");
        String cronjobReadJson = new BufferedReader(new InputStreamReader(cronjobReadInputStream)).lines().collect(Collectors.joining());

        CronJob cronjob = objectMapper.readValue(cronjobReadJson, CronJob.class);
        when(cronJobCreateApi.cronjob(yaml)).thenReturn(cronjob);

        String json = objectMapper.writeValueAsString(created(cronjob).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.TEXT_PLAIN_VALUE).content(yaml))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void cronjobRead() throws Exception {
        when(cronJobReadApi.read("default", "hello")).thenReturn(cronjob());

        InputStream inputStream = getClass().getResourceAsStream("cronjob-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        Job value = objectMapper.readValue(json, Job.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{cronjob}"), "default", "hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void cronjobListRead() throws Exception {
        when(cronJobReadApi.read("default", Map.of())).thenReturn(cronjobList());

        InputStream inputStream = getClass().getResourceAsStream("cronjobList-read.json");
        String json = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        JobList value = objectMapper.readValue(json, JobList.class);
        String _json = objectMapper.writeValueAsString(ok(value).getBody());

        String body = objectMapper.writeValueAsString(Map.of());
        this.mockMvc.perform(MockMvcRequestBuilders
                .get(PATH.concat("/{namespace}"), "default").contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public CronJobList cronjobList() {

        CronJobListBuilder cronjobListBuilder = new CronJobListBuilder();
        CronJobList cronjobList = cronjobListBuilder.withApiVersion("batch/v1")
                .withKind("CronJobList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("18881").build())
                .withItems(cronjob())
                .build();

        return cronjobList;
    }

    public CronJob cronjob() {
        CronJobBuilder cronjobBuilder = new CronJobBuilder();

        CronJob cronjob = cronjobBuilder.withApiVersion("batch/v1")
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

        return cronjob;
    }


}
