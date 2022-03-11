package io.hotcloud.kubernetes.api.equianlent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KubectlApi {

    List<HasMetadata> apply(String namespace, String yaml);

    Boolean delete(String namespace, String yaml);

    Boolean upload(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action);

    Boolean download(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action);

    Boolean portForward(String namespace, String pod, @Nullable String ipv4Address, Integer containerPort, Integer localPort, @Nullable Long alive, @Nullable TimeUnit unit);

    List<Event> events(String namespace);

    default Event events(String namespace, String name) {
        Assert.hasText(name, "Event name is null", 400);
        return this.events(namespace)
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }
}
