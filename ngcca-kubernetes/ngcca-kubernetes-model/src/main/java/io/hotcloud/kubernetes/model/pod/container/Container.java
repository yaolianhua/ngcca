package io.hotcloud.kubernetes.model.pod.container;

import io.hotcloud.kubernetes.model.Resources;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Container {

    @NotBlank(message = "container image is null")
    private String image;
    @NotBlank(message = "container name is null")
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
