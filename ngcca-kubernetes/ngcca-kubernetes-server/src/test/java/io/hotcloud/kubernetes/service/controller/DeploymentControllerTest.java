package io.hotcloud.kubernetes.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.*;
import io.hotcloud.kubernetes.api.DeploymentApi;
import io.hotcloud.kubernetes.model.RollingAction;
import io.hotcloud.kubernetes.model.YamlBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = DeploymentController.class)
@MockBeans(value = {
        @MockBean(classes = {
                DeploymentApi.class
        })
})
public class DeploymentControllerTest {

    public final static String PATH = "/v1/kubernetes/deployments";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DeploymentApi deploymentApi;

    @Test
    public void deploymentUpdateImages() throws Exception {
        LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        Map<String, String> images = Map.of("hotcloud", "yaolianhua/hotcloud:latest",
                "other-container", "other-image");
        multiValueMap.setAll(images);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .patch(PATH.concat("/{namespace}/{deployment}/images"), "default", "hotcloud")
                        .params(multiValueMap))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(deploymentApi, times(1)).imageUpdate(images, "default", "hotcloud");
    }

    @Test
    public void deploymentUpdateImage() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .patch(PATH.concat("/{namespace}/{deployment}/image"), "default", "hotcloud")
                        .param("image", "yaolianhua/hotcloud:latest"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(deploymentApi, times(1)).imageUpdate("default", "hotcloud", "yaolianhua/hotcloud:latest");
    }

    @Test
    public void deploymentRolling() throws Exception {
        for (RollingAction action : RollingAction.values()) {

            this.mockMvc.perform(MockMvcRequestBuilders
                            .patch(PATH.concat("/{namespace}/{deployment}/rolling"), "default", "hotcloud")
                            .param("action", action.name()))
                    .andDo(print())
                    .andExpect(status().isAccepted());
            //was invoked one time
            verify(deploymentApi, times(1)).rolling(action, "default", "hotcloud");
        }
    }

    @Test
    public void deploymentScale() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.patch(PATH.concat("/{namespace}/{deployment}/{count}/scale"), "default", "hotcloud", 3))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(deploymentApi, times(1)).scale("default", "hotcloud", 3, false);
    }

    @Test
    public void deploymentDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{deployment}"), "default", "hotcloud"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(deploymentApi, times(1)).delete("default", "hotcloud");
    }

    @Test
    public void deploymentCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("deployment-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream deploymentReadInputStream = getClass().getResourceAsStream("deployment-read.json");
        String deploymentReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(deploymentReadInputStream))).lines().collect(Collectors.joining());

        Deployment deployment = objectMapper.readValue(deploymentReadJson, Deployment.class);
        when(deploymentApi.create(yaml)).thenReturn(deployment);

        String json = objectMapper.writeValueAsString(deployment);

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void deploymentRead() throws Exception {
        when(deploymentApi.read("default", "hotcloud")).thenReturn(deployment());

        InputStream inputStream = getClass().getResourceAsStream("deployment-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        Deployment value = objectMapper.readValue(json, Deployment.class);
        String _json = objectMapper.writeValueAsString(value);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{deployment}"), "default", "hotcloud"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    @Test
    public void deploymentListRead() throws Exception {
        when(deploymentApi.read("default", Map.of())).thenReturn(deploymentList());

        InputStream inputStream = getClass().getResourceAsStream("deploymentList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        DeploymentList value = objectMapper.readValue(json, DeploymentList.class);
        String _json = objectMapper.writeValueAsString(value);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public DeploymentList deploymentList() {

        DeploymentListBuilder deploymentListBuilder = new DeploymentListBuilder();

        return deploymentListBuilder.withApiVersion("apps/v1")
                .withKind("DeploymentList")
                .withMetadata(new ListMetaBuilder().withResourceVersion("206808").build())
                .withItems(deployment())
                .build();
    }

    public Deployment deployment() {
        DeploymentBuilder deploymentBuilder = new DeploymentBuilder();

        return deploymentBuilder.withApiVersion("apps/v1")
                .withKind("Deployment")
                .withMetadata(new ObjectMetaBuilder()
                        .withName("hotcloud")
                        .withNamespace("default")
                        .withLabels(Map.of("k8s-app", "hotcloud"))
                        .build())
                .withSpec(new DeploymentSpecBuilder()
                        .withProgressDeadlineSeconds(600)
                        .withReplicas(1)
                        .withRevisionHistoryLimit(10)
                        .withSelector(new LabelSelectorBuilder().withMatchLabels(Map.of("k8s-app", "hotcloud")).build())
                        .withStrategy(new DeploymentStrategyBuilder()
                                .withRollingUpdate(new RollingUpdateDeploymentBuilder()
                                        .withMaxSurge(new IntOrString("25%"))
                                        .withMaxUnavailable(new IntOrString("25%"))
                                        .build())
                                .withType("RollingUpdate")
                                .build())
                        .withTemplate(new PodTemplateSpecBuilder()
                                .withMetadata(new ObjectMetaBuilder().withLabels(Map.of("k8s-app", "hotcloud")).build())
                                .withSpec(
                                        new PodSpecBuilder()
                                                .withAffinity(new AffinityBuilder().withNodeAffinity(
                                                        new NodeAffinityBuilder().withRequiredDuringSchedulingIgnoredDuringExecution(
                                                                new NodeSelectorBuilder().withNodeSelectorTerms(
                                                                        List.of(new NodeSelectorTermBuilder().withMatchExpressions(List.of(new NodeSelectorRequirementBuilder()
                                                                                                .withKey("node-role.kubernetes.io/master")
                                                                                                .withOperator("Exists").build())
                                                                                        )
                                                                                        .build()
                                                                        )
                                                                ).build()
                                                        ).build()
                                                ).build())
                                                .withContainers(
                                                        List.of(
                                                                new ContainerBuilder()
                                                                        .withImage("harbor.local:7000/hotcloud/hotcloud:0.1.20211105115017")
                                                                        .withImagePullPolicy("IfNotPresent")
                                                                        .withLivenessProbe(
                                                                                new ProbeBuilder()
                                                                                        .withFailureThreshold(1)
                                                                                        .withInitialDelaySeconds(180)
                                                                                        .withPeriodSeconds(120)
                                                                                        .withSuccessThreshold(1)
                                                                                        .withTimeoutSeconds(1)
                                                                                        .withHttpGet(new HTTPGetActionBuilder()
                                                                                                .withPath("/livez")
                                                                                                .withPort(new IntOrString("http"))
                                                                                                .withScheme("HTTP")
                                                                                                .build())
                                                                                        .build()
                                                                        )
                                                                        .withName("hotcloud")
                                                                        .withPorts(new ContainerPortBuilder()
                                                                                .withContainerPort(8080)
                                                                                .withName("http")
                                                                                .withProtocol("TCP")
                                                                                .build())
                                                                        .withReadinessProbe(
                                                                                new ProbeBuilder()
                                                                                        .withFailureThreshold(1)
                                                                                        .withInitialDelaySeconds(120)
                                                                                        .withPeriodSeconds(60)
                                                                                        .withSuccessThreshold(1)
                                                                                        .withTimeoutSeconds(1)
                                                                                        .withHttpGet(new HTTPGetActionBuilder()
                                                                                                .withPath("/readyz")
                                                                                                .withPort(new IntOrString("http"))
                                                                                                .withScheme("HTTP")
                                                                                                .build())
                                                                                        .build()
                                                                        )
                                                                        .withResources(
                                                                                new ResourceRequirementsBuilder()
                                                                                        .withLimits(Map.of("cpu", Quantity.parse("200m"), "memory", Quantity.parse("256Mi")))
                                                                                        .withRequests(Map.of("cpu", Quantity.parse("100m"), "memory", Quantity.parse("128Mi")))
                                                                                        .build()
                                                                        )
                                                                        .withSecurityContext(new SecurityContextBuilder().withPrivileged(true).build())
                                                                        .withTerminationMessagePath("/dev/termination-log")
                                                                        .withTerminationMessagePolicy("File")
                                                                        .withVolumeMounts(
                                                                                new VolumeMount[]{
                                                                                        new VolumeMountBuilder().withMountPath("/hotcloud/config").withName("hotcloud-volume").withReadOnly(true).build(),
                                                                                        new VolumeMountBuilder().withMountPath("/root/.kube/config").withName("kubeconfig").withReadOnly(true).build(),
                                                                                }
                                                                        )
                                                                        .build()
                                                        ))
                                                .withDnsPolicy("ClusterFirst")
                                                .withRestartPolicy("Always")
                                                .withSchedulerName("default-scheduler")
                                                .withSecurityContext(new PodSecurityContext())
                                                .withTerminationGracePeriodSeconds(30L)
                                                .withTolerations(new TolerationBuilder().withEffect("NoSchedule").withOperator("Exists").build())
                                                .withVolumes(
                                                        new Volume[]{
                                                                new VolumeBuilder().withConfigMap(
                                                                                new ConfigMapVolumeSourceBuilder()
                                                                                        .withDefaultMode(420)
                                                                                        .withName("hotcloud-config")
                                                                                        .withItems(
                                                                                                new KeyToPathBuilder().withKey("config").withPath("application.yml").build()
                                                                                        ).build())
                                                                        .withName("hotcloud-volume")
                                                                        .build(),
                                                                new VolumeBuilder().withHostPath(
                                                                                new HostPathVolumeSourceBuilder()
                                                                                        .withPath("/root/.kube/config")
                                                                                        .withType("").build())
                                                                        .withName("kubeconfig")
                                                                        .build()
                                                        }
                                                )
                                                .build()

                                ).build()

                        ).build()


                ).build();
    }


}
