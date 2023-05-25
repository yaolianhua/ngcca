package io.hotcloud.kubernetes.server.equivalent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.hotcloud.common.log.Log;
import io.hotcloud.kubernetes.api.KubectlApi;
import io.hotcloud.kubernetes.api.PodApi;
import io.hotcloud.kubernetes.model.CopyAction;
import io.hotcloud.kubernetes.model.RequestParamAssertion;
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

@Component
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
            Log.debug(this, yaml, String.format("%s %s create or replace, namespace %s", metadata.getKind(), metadata.getMetadata().getName(), namespace));
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

        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");

        //valid pod exist
        checkPodExist(namespace, pod);

        try {
            if (Objects.equals(action, CopyAction.FILE)) {
                Log.debug(this, null, String.format("Upload local file '%s' to  '%s' of Pod [%s], container '%s'", source, target, pod, container));
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
                Log.debug(this, null, String.format("Upload local dir '%s' to '%s' of Pod [%s], container '%s'", source, target, pod, container));
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
            Log.error(this, null,String.format("upload error '%s'", e.getMessage()));
            throw new RuntimeException(e.getMessage());
        }

        return false;
    }

    @Override
    public Boolean download(String namespace, String pod, @Nullable String container, String source, String target, CopyAction action) {
        RequestParamAssertion.assertNamespaceNotNull(namespace);
        RequestParamAssertion.assertResourceNameNotNull(pod);
        Assert.hasText(source, "source path  is null");
        Assert.hasText(target, "target path is null");

        //valid pod exist
        checkPodExist(namespace, pod);

        try {
            if (Objects.equals(action, CopyAction.FILE)) {
                Log.debug(this, null, String.format("Download file '%s' from Pod [%s] to local '%s', container '%s'", source, pod, target, container));
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
                Log.debug(this, null, String.format("Download dir '%s' from Pod [%s] to local '%s', container '%s'", source, pod, target, container));
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
            Log.error(this, null, String.format("download error '%s'", e.getMessage()));
            throw new RuntimeException(e.getMessage());
        }

        return false;
    }

    private void checkPodExist(String namespace, String pod) {
        Pod read = podApi.read(namespace, pod);
        Assert.notNull(read, String.format("Pod '%s' can not be found in namespace '%s'", pod, namespace));
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
        checkPodExist(namespace, pod);

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

                Log.info(this, null, String.format("Port forward open for %s %s, ip='%s', containerPort='%s', localPort='%s'", lR, unitR.name().toLowerCase(), ipR, containerPort, localPort));
                boolean await = countDownLatch.await(lR, unitR);
                if (!await) {
                    Log.info(this, null, "latch terminated before it's count reaching zero");
                }
                forward.close();
                Log.info(this, null, String.format("Closing port forward, ip='%s', containerPort='%s', localPort='%s'", ipR, containerPort, localPort));
            } catch (Exception e) {
                Log.error(this, null, String.format("%s: %s", e.getMessage(), e.getCause().getMessage()));
                errorReference.set(String.format("%s: %s", e.getMessage(), e.getCause().getMessage()));
                resultBoolean.set(false);
                countDownLatch.countDown();
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }

        });

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
