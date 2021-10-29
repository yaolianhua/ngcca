package io.hotCloud.core.kubernetes.job;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class JobMetadata {

    private String namespace = "default";

    @NotBlank(message = "Deployment name is empty")
    private String name;

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();


    public JobMetadata(String namespace, String name,
                       Map<String, String> labels,
                       Map<String, String> annotations) {
        this.namespace = namespace;
        this.name = name;
        this.labels = labels;
        this.annotations = annotations;
    }

    public JobMetadata() {
    }
}
