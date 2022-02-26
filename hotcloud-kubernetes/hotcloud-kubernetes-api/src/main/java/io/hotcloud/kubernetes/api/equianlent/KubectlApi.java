package io.hotcloud.kubernetes.api.equianlent;

import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 *
 **/
public interface KubectlApi {

    List<HasMetadata> apply(String namespace, String yaml);

    Boolean delete(String namespace, String yaml);
}
