package io.hotcloud.core.kubernetes.pod.container;

import io.hotcloud.core.kubernetes.Resources;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Container {

    @NotBlank(message = "container image is empty")
    private String image;
    @NotBlank(message = "container name is empty")
    private String name;

    private ImagePullPolicy imagePullPolicy = ImagePullPolicy.IfNotPresent;
    private Probe readinessProbe;
    private Probe livenessProbe;
    private Probe startupProbe;

    private SecurityContext securityContext;
    private Boolean stdin;
    private Resources resources;

    private List<Port> ports = new ArrayList<>();

    private List<Env> env = new ArrayList<>();

    private List<EnvFrom> envFrom = new ArrayList<>();

    private List<String> args = new ArrayList<>();

    private List<String> command = new ArrayList<>();

    private List<VolumeMount> volumeMounts = new ArrayList<>();

}
