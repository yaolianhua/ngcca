package io.hotcloud.kubernetes.server.equivalent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.hotcloud.kubernetes.api.equianlent.CopyAction;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.pod.PodApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yaolianhua789@gmail.com
 *
 **/
@Component
@Slf4j
public class KubectlEquivalent implements KubectlApi {

    private final KubernetesClient fabric8Client;
    private final ExecutorService executorService;
    private final PodApi podApi;

    public KubectlEquivalent(KubernetesClient fabric8Client,
                             ExecutorService executorService,
                             PodApi podApi) {
        this.fabric8Client = fabric8Client;
        this.executorService = executorService;
        this.podApi = podApi;
    }

    @Override
    public List<HasMetadata> apply(String namespace, String yaml) {
        Assert.hasText(yaml, "Yaml is null");

        InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());
        List<HasMetadata> hasMetadata = StringUtils.hasText(namespace) ?
                fabric8Client.load(inputStream).inNamespace(namespace).createOrReplace() :
                fabric8Client.load(inputStream).createOrReplace();

        for (HasMetadata metadata : hasMetadata) {
            log.debug("{} '{}' create or replace", metadata.getKind(), metadata.getMetadata().getName());
        }

        return hasMetadata;
    }

    @Override
    public Boolean delete(String namespace, String yaml) {
        Assert.hasText(yaml, "Yaml is null");

        InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());

        List<StatusDetails> details = StringUtils.hasText(namespace) ?
                fabric8Client.load(inputStream).inNamespace(namespace).delete() :
                fabric8Client.load(inputStream).delete();

        return !CollectionUtils.isEmpty(details);
    }

    @Override
    public Boolean upload(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action) {

        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");

        //valid pod exist
        Pod read = podApi.read(namespace, pod);
        Assert.notNull(read, String.format("Pod '%s' can not be found in namespace '%s'", pod, namespace));

        try {
            if (Objects.equals(action, CopyAction.FILE)) {
                log.debug("Upload local file '{}' to  '{}' of Pod [{}], container '{}'", source, target, pod, container);
                return StringUtils.hasText(container) ?
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .inContainer(container)
                                .file(target)
                                .upload(Path.of(source)) :
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .file(target)
                                .upload(Path.of(source));
            }
            if (Objects.equals(action, CopyAction.DIRECTORY)) {
                log.debug("Upload local dir '{}' to '{}' of Pod [{}], container '{}'", source, target, pod, container);
                return StringUtils.hasText(container) ?
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .inContainer(container)
                                .dir(target)
                                .upload(Path.of(source)) :
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .dir(target)
                                .upload(Path.of(source));
            }
        } catch (Exception e) {
            log.error("upload error '{}'", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

        return false;
    }

    @Override
    public Boolean download(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action) {
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(pod, "pod name is null");
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");

        //valid pod exist
        Pod read = podApi.read(namespace, pod);
        Assert.notNull(read, String.format("Pod '%s' can not be found in namespace '%s'", pod, namespace));

        try {
            if (Objects.equals(action, CopyAction.FILE)) {
                log.debug("Download file '{}' from Pod [{}] to local '{}', container '{}'", source, pod, target, container);
                return StringUtils.hasText(container) ?
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .inContainer(container)
                                .file(source)
                                .copy(Path.of(target)) :
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .file(source)
                                .copy(Path.of(target));
            }
            if (Objects.equals(action, CopyAction.DIRECTORY)) {
                log.debug("Download dir '{}' from Pod [{}] to local '{}', container '{}'", source, pod, target, container);
                return StringUtils.hasText(container) ?
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .inContainer(container)
                                .dir(source)
                                .copy(Path.of(target)) :
                        fabric8Client.pods()
                                .inNamespace(namespace)
                                .withName(pod)
                                .dir(source)
                                .copy(Path.of(target));
            }
        } catch (Exception e) {
            log.error("download error '{}'", e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }

        return false;
    }

    @Override
    public Boolean portForward(String namespace, String pod, @Nullable String ipv4Address, Integer containerPort, Integer localPort, @Nullable Long time, @Nullable TimeUnit timeUnit) {

        Assert.notNull(containerPort, "containerPort is null");
        Assert.notNull(localPort, "localPort is null");

        //valid ipv4Address
        AtomicReference<String> ipReference = new AtomicReference<>(ipv4Address);
        String ipR = StringUtils.hasText(ipReference.get()) ? ipReference.get() : "127.0.0.1";

        Assert.state(InetAddressValidator.getInstance().isValid(ipR), "invalid ipv4 address");

        //valid pod exist
        Pod read = podApi.read(namespace, pod);
        Assert.notNull(read, String.format("Pod '%s' can not be found in namespace '%s'", pod, namespace));

        //return result
        AtomicBoolean resultBoolean = new AtomicBoolean(true);
        //error reference
        AtomicReference<String> errorReference = new AtomicReference<>();
        //countDownLatch
        CountDownLatch countDownLatch = new CountDownLatch(1);

        AtomicReference<InetAddress> inetAddressReference = new AtomicReference<>();
        try {
            inetAddressReference.set(InetAddress.getByName(ipR));
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        //valid timeunit and alive times
        AtomicReference<TimeUnit> unitReference = new AtomicReference<>(timeUnit);
        AtomicLong aliveLong = new AtomicLong(time == null ? 0L : time);

        TimeUnit unitR = unitReference.get() == null ? TimeUnit.MINUTES : unitReference.get();
        long lR = aliveLong.get() > 0 ? aliveLong.get() : 10L;

        executorService.execute(() -> {
            try {
                LocalPortForward forward = fabric8Client.pods()
                        .inNamespace(namespace)
                        .withName(pod)
                        .portForward(containerPort, inetAddressReference.get(), localPort);
                countDownLatch.countDown();

                log.debug("Port forward open for {} {}, ip='{}', containerPort='{}', localPort='{}'", lR, unitR.name().toLowerCase(), ipR, containerPort, localPort);
                unitR.sleep(lR);

                forward.close();
                log.debug("Closing port forward, ip='{}', containerPort='{}', localPort='{}'", ipR, containerPort, localPort);
            } catch (Exception e) {
                log.error("{}: {}", e.getMessage(), e.getCause().getMessage());
                errorReference.set(String.format("%s: %s", e.getMessage(), e.getCause().getMessage()));
                resultBoolean.set(false);
                countDownLatch.countDown();
            }

        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            //
        }
        Assert.state(!StringUtils.hasText(errorReference.get()), errorReference.get());
        return resultBoolean.get();
    }

    @Override
    public List<Event> events(String namespace) {
        Assert.hasText(namespace, "namespace is null");

        return fabric8Client.v1().events()
                .inNamespace(namespace)
                .list()
                .getItems();
    }

    @Override
    public List<Event> events() {
        return fabric8Client.v1().events()
                .inAnyNamespace()
                .list()
                .getItems();
    }
}
