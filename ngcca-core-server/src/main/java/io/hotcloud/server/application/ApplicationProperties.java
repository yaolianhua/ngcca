package io.hotcloud.server.application;

import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.utils.Log;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "application")
@Properties(prefix = CONFIG_PREFIX + "application")
@Data
@Configuration(proxyBeanMethods = false)
public class ApplicationProperties {

    public static final String PROPERTIES_TYPE_NAME = CONFIG_PREFIX + "application.deployment-notify-mode";
    public static final String RABBITMQ = "rabbitmq";
    public static final String IN_PROCESS = "inProcess";

    private NotifyMode deploymentNotifyMode = NotifyMode.inProcess;
    private String dotSuffixDomain = ".k8s-cluster.local";

    @PostConstruct
    public void print() {
        if (NotifyMode.inProcess.equals(deploymentNotifyMode)) {
            Log.info(ApplicationProperties.class.getName(), "【Load application properties. used notify mode in-process】");
            return;
        }
        if (NotifyMode.rabbitmq.equals(deploymentNotifyMode)) {
            Log.info(ApplicationProperties.class.getName(), "【Load application properties. used notify mode rabbitmq】");
        }
    }

    public String getDotSuffixDomain() {
        if (StringUtils.hasText(dotSuffixDomain) && !dotSuffixDomain.startsWith(".")) {
            return "." + dotSuffixDomain;
        }
        return dotSuffixDomain;
    }

    enum NotifyMode {
        //
        rabbitmq,
        //
        inProcess

    }
}
