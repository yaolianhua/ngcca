package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.utils.Log;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = CONFIG_PREFIX + "buildpack.imagebuild")
@Data
@Properties(prefix = CONFIG_PREFIX + "buildpack.imagebuild")
public class BuildPackImagebuildProperties {

    public static final String PROPERTIES_TYPE_NAME = CONFIG_PREFIX + "buildpack.imagebuild.notify-mode";
    public static final String RABBITMQ = "rabbitmq";
    public static final String IN_PROCESS = "inProcess";

    private NotifyMode notifyMode = NotifyMode.inProcess;

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

    enum NotifyMode {
        //
        rabbitmq,
        //
        inProcess

    }
}
