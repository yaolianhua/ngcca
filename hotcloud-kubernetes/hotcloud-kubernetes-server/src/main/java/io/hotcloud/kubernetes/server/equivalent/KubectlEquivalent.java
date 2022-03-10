package io.hotcloud.kubernetes.server.equivalent;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.common.Validator;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.pod.PodReadApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
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
    private final PodReadApi podReadApi;

    public KubectlEquivalent(KubernetesClient fabric8Client,
                             ExecutorService executorService,
                             PodReadApi podReadApi) {
        this.fabric8Client = fabric8Client;
        this.executorService = executorService;
        this.podReadApi = podReadApi;
    }

    @Override
    public List<HasMetadata> apply(String namespace, String yaml) {
        Assert.hasText(yaml, "Yaml is null", 400);

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
        Assert.hasText(yaml, "Yaml is null", 400);

        InputStream inputStream = new ByteArrayInputStream(yaml.getBytes());
        Boolean deleted = StringUtils.hasText(namespace) ?
                fabric8Client.load(inputStream).inNamespace(namespace).delete() :
                fabric8Client.load(inputStream).delete();

        return deleted;
    }

    @Override
    public Boolean portForward(String namespace,
                               String pod,
                               String ipv4Address,
                               Integer containerPort,
                               Integer localPort,
                               Long time,
                               TimeUnit timeUnit) {

        Assert.notNull(containerPort, "containerPort is null", 400);
        Assert.notNull(localPort, "localPort is null", 400);

        //valid ipv4Address
        AtomicReference<String> ipReference = new AtomicReference<>(ipv4Address);
        String ipR = StringUtils.hasText(ipReference.get()) ? ipReference.get() : "127.0.0.1";
        Assert.argument(Validator.validIpv4(ipR), "invalid ipv4 address");

        //valid pod exist
        Pod read = podReadApi.read(namespace, pod);
        Assert.notNull(read, String.format("Pod '%s' can not be found in namespace '%s'", pod, namespace), 404);

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
            throw new HotCloudException(e.getMessage(), 400);
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
        Assert.state(!StringUtils.hasText(errorReference.get()), errorReference.get(), 400);
        return resultBoolean.get();
    }

    @Override
    public List<Event> events(String namespace) {
        Assert.hasText(namespace, "namespace is null", 400);
        List<Event> items = fabric8Client.v1().events()
                .inNamespace(namespace)
                .list()
                .getItems();

        return items;
    }
}
