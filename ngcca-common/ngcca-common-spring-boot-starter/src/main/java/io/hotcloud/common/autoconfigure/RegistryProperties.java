package io.hotcloud.common.autoconfigure;

import io.hotcloud.common.model.Properties;
import io.hotcloud.common.model.utils.Log;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@ConfigurationProperties(prefix = CONFIG_PREFIX + "registry")
@Data
@Properties(prefix = CONFIG_PREFIX + "registry")
public class RegistryProperties {

    /**
     * Registry address. you can set this value e.g.
     * <ul>
     * <li>gcr.io
     * <li>127.0.0.1
     * <li>192.168.0.1:5000
     * <li>mycompany-docker-virtual.jfrog.io
     * </ul>
     */
    private String url;

    /**
     * Image build registry namespace
     */
    private String imagebuildNamespace;
    /**
     * Registry credential with username
     */
    private String username;
    /**
     * Registry credential with password
     */
    private String password;

    @PostConstruct
    public void print() {
        Assert.hasText(url, "Registry url is null");
        Assert.hasText(imagebuildNamespace, "Registry imagebuild namespace is null");
        Log.info(RegistryProperties.class.getName(), String.format("【Load Registry proeprties. registry='%s' imagebuild-namespace='%s'】", url, imagebuildNamespace));
    }
}
