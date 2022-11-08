package io.hotcloud.buildpack.server.core;

import io.hotcloud.common.model.Log;
import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "buildpack.imagebuild")
@Data
@Properties(prefix = "buildpack.imagebuild")
public class BuildPackImagebuildProperties {

    public static final String PROPERTIES_TYPE_NAME = "buildpack.imagebuild.notify-mode";
    public static final String RABBITMQ = "rabbitmq";
    public static final String IN_PROCESS = "inProcess";

    private NotifyMode notifyMode = NotifyMode.inProcess;
    enum NotifyMode {
        //
        rabbitmq,
        //
        inProcess

    }

    @PostConstruct
    public void print() {
        if (NotifyMode.inProcess.equals(notifyMode)) {
            Log.info(BuildPackImagebuildProperties.class.getName(), "【Load image build properties. used notify mode in-process】");
            return;
        }
        if (NotifyMode.rabbitmq.equals(notifyMode)) {
            Log.info(BuildPackImagebuildProperties.class.getName(), "【Load image build properties. used notify mode rabbitmq】");
        }
    }
}
