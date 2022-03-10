package io.hotcloud.kubernetes.api.pod;

import io.fabric8.kubernetes.api.model.Pod;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PodUpdateApi {

    Pod addAnnotations(String namespace, String pod, Map<String, String> annotations);

    Pod addLabels(String namespace, String pod, Map<String, String> labels);
}
