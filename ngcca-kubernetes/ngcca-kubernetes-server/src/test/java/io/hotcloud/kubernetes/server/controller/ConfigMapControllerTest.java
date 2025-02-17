package io.hotcloud.kubernetes.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.*;
import io.hotcloud.kubernetes.api.ConfigMapApi;
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
import java.util.Collections;
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
@WebMvcTest(value = ConfigMapController.class)
@MockBeans(value = {
        @MockBean(classes = {
                ConfigMapApi.class
        })
})
public class ConfigMapControllerTest {

    public final static String PATH = "/v1/kubernetes/configmaps";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ConfigMapApi configMapApi;

    @Test
    public void configMapCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("configMap-create.yaml");
        String yaml = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining("\n"));

        InputStream configMapReadInputStream = getClass().getResourceAsStream("configMap-read.json");
        String configMapReadJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(configMapReadInputStream))).lines().collect(Collectors.joining());

        ConfigMap configMap = objectMapper.readValue(configMapReadJson, ConfigMap.class);
        when(configMapApi.create(yaml)).thenReturn(configMap);

        String json = objectMapper.writeValueAsString(configMap);

        String yamlBody = objectMapper.writeValueAsString(YamlBody.of(yaml));
        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(PATH.concat("/yaml")).contentType(MediaType.APPLICATION_JSON).content(yamlBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));

    }

    @Test
    public void configMapDelete() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PATH.concat("/{namespace}/{configmap}"), "default", "hotcloud-config"))
                .andDo(print())
                .andExpect(status().isAccepted());
        //was invoked one time
        verify(configMapApi, times(1)).delete("default", "hotcloud-config");
    }

    @Test
    public void configMapRead() throws Exception {
        when(configMapApi.read("default", "hotcloud-config"))
                .thenReturn(configMap());

        InputStream inputStream = getClass().getResourceAsStream("configMap-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());

        ConfigMap value = objectMapper.readValue(json, ConfigMap.class);
        String _json = objectMapper.writeValueAsString(value);
        this.mockMvc.perform(MockMvcRequestBuilders.get(PATH.concat("/{namespace}/{configmap}"), "default", "hotcloud-config"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public ConfigMap configMap() {
        ConfigMapBuilder builder = new ConfigMapBuilder();
        return builder.withImmutable(true)
                .withMetadata(new ObjectMetaBuilder().withName("hotcloud-config").withNamespace("default").withLabels(Collections.singletonMap("k8s-app", "hotcloud")).build())
                .withData(Collections.singletonMap("config", "logging:\n  level:\n    io.hotCloud.server: debug\nkubernetes:\n  in-cluster: true\n"))
                .withApiVersion("v1")
                .withKind("ConfigMap")
                .build();
    }

    @Test
    public void configMapListRead() throws Exception {
        when(configMapApi.read("default", Collections.emptyMap()))
                .thenReturn(configMapList());

        InputStream inputStream = getClass().getResourceAsStream("configMapList-read.json");
        String json = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines().collect(Collectors.joining());
        ConfigMapList configMapList = objectMapper.readValue(json, ConfigMapList.class);
        String _json = objectMapper.writeValueAsString(configMapList);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get(PATH.concat("/{namespace}"), "default"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(_json));
    }

    public ConfigMapList configMapList() {
        ConfigMapListBuilder builder = new ConfigMapListBuilder();

        ConfigMap item1 = new ConfigMapBuilder()
                .withApiVersion("v1")
                .withKind("ConfigMap")
                .withData(Map.of("config.conf", "apiVersion: kubeproxy.config.k8s.io/v1alpha1\nbindAddress: 0.0.0.0\nbindAddressHardFail: false\nclientConnection:\n  acceptContentTypes: \"\"\n  burst: 0\n  contentType: \"\"\n  kubeconfig: /var/lib/kube-proxy/kubeconfig.conf\n  qps: 0\nclusterCIDR: 10.244.0.0/16\nconfigSyncPeriod: 0s\nconntrack:\n  maxPerCore: null\n  min: null\n  tcpCloseWaitTimeout: null\n  tcpEstablishedTimeout: null\ndetectLocalMode: \"\"\nenableProfiling: false\nhealthzBindAddress: \"\"\nhostnameOverride: \"\"\niptables:\n  masqueradeAll: false\n  masqueradeBit: null\n  minSyncPeriod: 0s\n  syncPeriod: 0s\nipvs:\n  excludeCIDRs: null\n  minSyncPeriod: 0s\n  scheduler: \"\"\n  strictARP: false\n  syncPeriod: 0s\n  tcpFinTimeout: 0s\n  tcpTimeout: 0s\n  udpTimeout: 0s\nkind: KubeProxyConfiguration\nmetricsBindAddress: \"\"\nmode: \"\"\nnodePortAddresses: null\noomScoreAdj: null\nportRange: \"\"\nshowHiddenMetricsForVersion: \"\"\nudpIdleTimeout: 0s\nwinkernel:\n  enableDSR: false\n  networkName: \"\"\n  sourceVip: \"\"",
                        "kubeconfig.conf", "apiVersion: v1\nkind: Config\nclusters:\n- cluster:\n    certificate-authority: /var/run/secrets/kubernetes.io/serviceaccount/ca.crt\n    server: https://192.168.56.100:6443\n  name: default\ncontexts:\n- context:\n    cluster: default\n    namespace: default\n    user: default\n  name: default\ncurrent-context: default\nusers:\n- name: default\n  user:\n    tokenFile: /var/run/secrets/kubernetes.io/serviceaccount/token"))
                .withMetadata(
                        new ObjectMetaBuilder()
                                .withAnnotations(Map.of("kubeadm.kubernetes.io/component-config.hash", "sha256:ebaf74345f16ace6e2565cd282228d551de662026675124f262f249005b1d6ff"))
                                .withLabels(Map.of("app", "kube-proxy"))
                                .withName("kube-proxy")
                                .withNamespace("kube-system")
                                .build()
                )
                .build();

        ConfigMap item2 = new ConfigMapBuilder()
                .withApiVersion("v1")
                .withKind("ConfigMap")
                .withData(Map.of("ClusterConfiguration", "apiServer:\n  extraArgs:\n    authorization-mode: Node,RBAC\n  timeoutForControlPlane: 4m0s\napiVersion: kubeadm.k8s.io/v1beta3\ncertificatesDir: /etc/kubernetes/pki\nclusterName: kubernetes\ncontrollerManager: {}\ndns: {}\netcd:\n  local:\n    dataDir: /var/lib/etcd\nimageRepository: k8s.gcr.io\nkind: ClusterConfiguration\nkubernetesVersion: v1.22.2\nnetworking:\n  dnsDomain: cluster.local\n  podSubnet: 10.244.0.0/16\n  serviceSubnet: 10.96.0.0/12\nscheduler: {}\n"))
                .withMetadata(
                        new ObjectMetaBuilder()
                                .withName("kubeadm-config")
                                .withNamespace("kube-system")
                                .build()
                )
                .build();
        return builder
                .withApiVersion("v1")
                .withKind("ConfigMapList")
                .withItems(List.of(item1, item2))
                .withMetadata(new ListMetaBuilder().build())
                .build();
    }
}
