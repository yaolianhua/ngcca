package io.hotcloud.kubernetes.api.equianlent;

import io.fabric8.kubernetes.api.model.HasMetadata;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubectlApi {

    List<HasMetadata> apply(String namespace, String yaml);

    Boolean delete(String namespace, String yaml);

    Boolean portForward(String namespace,
                        String pod,
                        String ip,
                        Integer containerPort,
                        Integer localPort,
                        long alive, TimeUnit unit);
}
