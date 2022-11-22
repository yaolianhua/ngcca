package io.hotcloud.kubernetes.client;

import io.hotcloud.kubernetes.client.configuration.KubernetesAgentAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({KubernetesAgentAutoConfiguration.class})
public @interface EnableKubernetesAgentClient {
}
