package io.hotcloud.buildpack.server.core;

import io.hotcloud.common.api.Log;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties("buildpack.registry")
@Configuration(proxyBeanMethods = false)
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
     * Image build registry project name.
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

    private String kanikoImageUrl;

    private String gitInitContainerImageUrl;

    @PostConstruct
    public void print() {
        Assert.hasText(url, "registry url is null");
        Assert.hasText(project, "image build registry project name is null");
        Assert.hasText(username, "registry credential with username is null");
        Assert.hasText(password, "registry credential with password is null");

        Log.info(BuildPackRegistryProperties.class.getName(),
                String.format("【Load BuildPack Registry Properties】registry-url='%s' registry-username='%s' registry-password='%s' kaniko-image='%s' git-image='%s'",
                        url,
                        username,
                        password,
                        kanikoImageUrl,
                        gitInitContainerImageUrl)
        );
    }

}
