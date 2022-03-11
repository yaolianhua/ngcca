package io.hotcloud.kubernetes.server.workload;

import io.hotcloud.kubernetes.HotCloudKubernetesApplicationTest;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudKubernetesApplicationTest.class
)
@ActiveProfiles("integration-test-local")
@Slf4j
public class PodWorkloadsWatchApiIT {

    static AtomicReference<Boolean> connected = new AtomicReference<>(false);

    @Autowired
    private PodApi podApi;
    /**
     * {@link PodWorkloadsWatchApiIT#watch()}
     */
    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        while (!connected.get()) {

            TimeUnit.SECONDS.sleep(5);
            new WebSocketClient(new URI("ws://localhost:8079/pub")) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("client opened: {}", serverHandshake);
                    connected.set(true);
                }

                @Override
                public void onMessage(String message) {
                    log.info("client received message: {}", message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("client connect failure: code={}, reason={}, remote={}", code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {

                }
            }.connect();
        }


    }

    @Test
    public void watch() throws ApiException, InterruptedException {

        podApi.pod("apiVersion: v1\n" +
                "kind: Pod\n" +
                "metadata:\n" +
                "  name: nginx\n" +
                "spec:\n" +
                "  containers:\n" +
                "  - name: nginx\n" +
                "    image: nginx:1.14.2\n" +
                "    ports:\n" +
                "    - containerPort: 80");
        TimeUnit.SECONDS.sleep(3);

        podApi.delete("default", "nginx");
        TimeUnit.SECONDS.sleep(3);

    }

}
