package io.hotcloud.buildpack.server;

import io.hotcloud.common.Assert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties("buildpack.registry")
@Configuration(proxyBeanMethods = false)
@Slf4j
public class BuildPackRegistryProperties {
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
     * Registry project name.
     */
    private String project;

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
        Assert.hasText(url, "registry url is null");
        Assert.notNull(project, "registry project name is null");
        Assert.hasText(username, "registry credential with username is null");
        Assert.hasText(password, "registry credential with password is null");

        log.info("【Load BuildPack Registry Properties】destination='{}' registry-username='{}' registry-password='{}'",
                String.format("%s/%s", url, project), username, password);
    }

}
