package io.hotcloud.buildpack.server.core;

import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.env.Properties;
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
@Properties(prefix = "buildpack.registry")
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

    /**
     * Kaniko image url
     */
    private String kanikoImageUrl;

    /**
     * Git image url
     */
    private String gitInitContainerImageUrl;
    private String alpineInitContainerImageUrl;
    /**
     * java application base image
     */
    private String javaBaseImage;

    @PostConstruct
    public void print() {
        Assert.hasText(url, "registry url is null");
        Assert.hasText(project, "image build registry project name is null");
        Assert.hasText(username, "registry credential with username is null");
        Assert.hasText(password, "registry credential with password is null");
        Assert.hasText(kanikoImageUrl, "kaniko image url is null");
        Assert.hasText(gitInitContainerImageUrl, "git image url is null");
        Assert.hasText(alpineInitContainerImageUrl, "alpine image url is null");
        Assert.hasText(javaBaseImage, "java base image is null");

        Log.info(BuildPackRegistryProperties.class.getName(),
                String.format("【Load BuildPack Registry Properties】registry-url='%s' registry-username='%s' registry-password='%s' kaniko-image='%s' git-image='%s' java-base-image='%s' alpine-image='%s'",
                        url,
                        username,
                        password,
                        kanikoImageUrl,
                        gitInitContainerImageUrl,
                        javaBaseImage,
                        alpineInitContainerImageUrl)
        );
    }

}
