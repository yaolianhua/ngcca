package io.hotcloud.message.server.websocket;

import io.hotcloud.message.server.HotCloudMessageApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudMessageApplicationTest.class
)
@Slf4j
public class WebSocketMessageSubscribeClientIT {
    WebSocketClient client;
    WebSocketStompClient stompClient;
    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        log.info("Setting up the client ...");
        client = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void givenWebSocket_whenMessage_thenVerifyMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        StompSessionHandler sessionHandler = new StompSessionHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return null;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("[{}] Connected to the WebSocket", session.getSessionId());
                session.subscribe("/pub", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Map.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        try {

                            assertThat(payload).isNotNull();
                            assertThat(payload).isInstanceOf(Map.class);

                            @SuppressWarnings("unchecked")
                            Map<String, Integer> map = (Map<String, Integer>) payload;

                            assertThat(map).containsKey("HPE");
                            assertThat(map.get("HPE")).isInstanceOf(Integer.class);
                        } catch (Throwable t) {
                            failure.set(t);
                            log.error("There is an exception ", t);
                        } finally {
                            session.disconnect();
                            latch.countDown();
                        }

                    }
                });
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
            }
        };
        stompClient.connect("ws://localhost:{port}/pub", sessionHandler, this.port);
        if (latch.await(60, TimeUnit.SECONDS)) {
            if (failure.get() != null) {
                fail("Assertion Failed", failure.get());
            }
        } else {
            fail("Could not receive the message on time");
        }
    }
}
