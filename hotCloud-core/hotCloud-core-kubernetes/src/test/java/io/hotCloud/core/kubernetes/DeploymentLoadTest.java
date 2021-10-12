package io.hotCloud.core.kubernetes;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.yaml.snakeyaml.Yaml;

/**
 * @author yaolianhua789@gmail.com
 **/
//@Slf4j
public class DeploymentLoadTest {


    public static void main(String[] args) {
        CoreV1Api api = new CoreV1Api();
        final AppsV1Api appsV1Api = new AppsV1Api();
//        appsV1Api.createNamespacedDeployment()
        final V1Deployment v1Deployment = deploymentYamlLoad();
        final String yaml = Yaml.dump(v1Deployment);
        System.out.println(yaml);

    }

    static V1Deployment deploymentYamlLoad(){

        V1Deployment v1Deployment = new V1Deployment();
        v1Deployment.setApiVersion("apps/v1");
        v1Deployment.setKind("Deployment");

        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        Map<String, String> annotations = new HashMap<>();
        annotations.put("app.cloudtogo.cn/blueprint","3cee459c-beb0-11eb-8dff-0242ac121204");
        annotations.put("app.cloudtogo.cn/path-in-blueprint",".bp");
        Map<String, String> deploymentLabels = new HashMap<>();
        deploymentLabels.put("app","my-nginx");
        deploymentLabels.put("version","v1");
        v1ObjectMeta.setName("my-nginx");
        v1ObjectMeta.setNamespace("default");
        v1ObjectMeta.setAnnotations(annotations);
        v1ObjectMeta.setLabels(deploymentLabels);


        v1Deployment.setMetadata(v1ObjectMeta);

        V1DeploymentSpec v1DeploymentSpec = new V1DeploymentSpec();
        v1DeploymentSpec.setReplicas(2);

        V1LabelSelector v1LabelSelector = new V1LabelSelector();
        Map<String, String> matchLabels = new HashMap<>();
        matchLabels.put("app","my-nginx");
        v1LabelSelector.setMatchLabels(matchLabels);

        v1DeploymentSpec.setSelector(v1LabelSelector);

        V1DeploymentStrategy v1DeploymentStrategy = new V1DeploymentStrategy();
        v1DeploymentStrategy.setType("RollingUpdate");

        v1DeploymentSpec.setStrategy(v1DeploymentStrategy);

        V1PodTemplateSpec v1PodTemplateSpec = new V1PodTemplateSpec();
        V1ObjectMeta podMetadata = new V1ObjectMeta();
        Map<String, String> podLabels = new HashMap<>();
        podLabels.put("app","my-nginx");
        podMetadata.setLabels(podLabels);
        v1PodTemplateSpec.setMetadata(podMetadata);

        V1PodSpec v1PodSpec = new V1PodSpec();

        final V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();

        v1NodeSelectorRequirement.setKey("kubernetes.io/hostname");
        v1NodeSelectorRequirement.setOperator("In");
        v1NodeSelectorRequirement.setValues(List.of("node1.qf5.7yi"));

        V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
        v1NodeSelectorTerm.setMatchExpressions(Collections.singletonList(v1NodeSelectorRequirement));
        V1NodeSelector v1NodeSelector = new V1NodeSelector();
        v1NodeSelector.nodeSelectorTerms(Collections.singletonList(v1NodeSelectorTerm));
        final V1NodeAffinity v1NodeAffinity = new V1NodeAffinity();
        v1NodeAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(v1NodeSelector);

        V1Affinity v1Affinity = new V1Affinity();
        v1Affinity.setNodeAffinity(v1NodeAffinity);
        v1PodSpec.setAffinity(v1Affinity);

        final V1ContainerPort v1ContainerPort = new V1ContainerPort();
        v1ContainerPort.setContainerPort(80);
        v1ContainerPort.setProtocol("TCP");
        final V1Probe v1Probe = new V1Probe();
        v1Probe.setFailureThreshold(3);
        v1Probe.setInitialDelaySeconds(1);
        v1Probe.setPeriodSeconds(5);
        v1Probe.setSuccessThreshold(1);
        V1TCPSocketAction v1TCPSocketAction = new V1TCPSocketAction();
        v1TCPSocketAction.setPort(new IntOrString(80));
        v1Probe.setTcpSocket(v1TCPSocketAction);
        v1Probe.setTimeoutSeconds(1);

        Map<String, Quantity> limits = new HashMap<>();
        limits.put("cpu", Quantity.fromString("10m"));
        limits.put("memory", Quantity.fromString("32Mi"));
        final V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();
        v1ResourceRequirements.setRequests(limits);
        v1ResourceRequirements.setLimits(limits);

        final V1Container v1Container = new V1Container();
        v1Container.setImage("nginx");
        v1Container.setImagePullPolicy("Always");
        v1Container.setName("my-nginx");
        v1Container.setPorts(Collections.singletonList(v1ContainerPort));
        v1Container.setReadinessProbe(v1Probe);
        v1Container.setResources(v1ResourceRequirements);

        v1PodSpec.setContainers(Collections.singletonList(v1Container));
        v1PodSpec.setDnsPolicy("ClusterFirst");
        v1PodSpec.setRestartPolicy("Always");
                v1PodSpec.setTerminationGracePeriodSeconds(30L);
        v1PodTemplateSpec.setSpec(v1PodSpec);

        v1DeploymentSpec.setTemplate(v1PodTemplateSpec);

        v1Deployment.setSpec(v1DeploymentSpec);

        return v1Deployment;
    }


}
