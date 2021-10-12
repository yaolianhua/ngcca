package io.hotCloud.core.kubernetes;

import io.hotCloud.core.kubernetes.affinity.Affinity;
import io.hotCloud.core.kubernetes.container.Container;
import io.hotCloud.core.kubernetes.volumes.Volume;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class PodTemplateSpec {

    public PodTemplateSpec() {
    }

    private Boolean automountServiceAccountToken;
    private Boolean enableServiceLinks;
    private Boolean hostIPC;
    private Boolean hostNetwork;
    private Boolean hostPID;
    private Boolean shareProcessNamespace;
    private Boolean setHostnameAsFQDN;

    private Long activeDeadlineSeconds;
    private Integer priority;
    private Long terminationGracePeriodSeconds;

    private List<Container> containers = new ArrayList<>();

    private List<Container> initContainers = new ArrayList<>();

    private List<Volume> volumes = new ArrayList<>();

    private String hostname;
    private String nodeName;
    private String preemptionPolicy;
    private String priorityClassName;
    /**
     * {@code serviceAccountName}
     */
    @Deprecated
    private String serviceAccount;
    private String serviceAccountName;
    private String subdomain;
    private String schedulerName;
    private String runtimeClassName;

    private PodSecurityContext securityContext;

    private Map<String, String> nodeSelector = new HashMap<>();

    private Map<String, String> overhead = new HashMap<>();

    private RestartPolicy restartPolicy = RestartPolicy.Always;

    private DnsPolicy dnsPolicy = DnsPolicy.ClusterFirst;

    private Affinity affinity;

    private List<Toleration> tolerations = new ArrayList<>();

    private List<ImagePullSecret> imagePullSecrets = new ArrayList<>();

    public enum  DnsPolicy{
        //
        ClusterFirstWithHostNet, ClusterFirst, Default ,None
    }
    public enum RestartPolicy{
        //
        Always, OnFailure, Never
    }

}
