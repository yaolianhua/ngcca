package io.hotcloud.core.kubernetes.workload;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class DaemonSetMetadata {
    private String namespace = "default";

    private String name;

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();


    public DaemonSetMetadata(String namespace, String name,
                             Map<String, String> labels,
                             Map<String, String> annotations) {
        this.namespace = namespace;
        this.name = name;
        this.labels = labels;
        this.annotations = annotations;
    }

    public DaemonSetMetadata() {
    }

}
