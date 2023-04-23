package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.Properties;
import io.hotcloud.common.utils.Log;
import io.hotcloud.module.buildpack.BuildPackConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "buildpack.storage")
@Data
@Properties(prefix = "buildpack.storage")
@Deprecated(since = "BuildPackApiV2")
public class BuildPackStorageProperties {

    /**
     * Storage type
     */
    private Type type = Type.hostPath;
    /**
     * nfs server. it can not be null if {@code type} is nfs
     */
    private String nfsServer;


    @PostConstruct
    public void print() {
        Log.info(BuildPackStorageProperties.class.getName(), String.format("【Load BuildPack Storage Properties】type = '%s', mount path = '%s'", type, retrieveStoragePath()));
    }

    public String retrieveStoragePath() {
        switch (type) {
            case hostPath:
                return BuildPackConstant.STORAGE_VOLUME_PATH;
            case nfs:
                Assert.hasText(nfsServer, "nfs server address is null");
                return String.format("%s:%s", nfsServer, BuildPackConstant.STORAGE_VOLUME_PATH);
            default:
                break;
        }

        return null;
    }

    public enum Type {
        //
        hostPath,
        nfs
    }

}
