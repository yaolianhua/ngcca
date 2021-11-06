package io.hotcloud.core.kubernetes.pod;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class PodTemplateMetadata {

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();

    public PodTemplateMetadata() {
    }

    public PodTemplateMetadata(Map<String, String> labels, Map<String, String> annotations) {
        this.labels = labels;
        this.annotations = annotations;
    }
}
