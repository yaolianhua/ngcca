package io.hotcloud.application.server;

import io.hotcloud.application.api.ApplicationConstant;
import io.hotcloud.common.Log;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Data
@Slf4j
public class ApplicationStorageProperties {

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
        Log.info(ApplicationStorageProperties.class.getName(), String.format("【Load Application Storage Properties】type = '%s', mount path = '%s'", type, retrieveStoragePath()));
    }

    public String retrieveStoragePath() {
        switch (type) {
            case hostPath:
                return ApplicationConstant.STORAGE_VOLUME_PATH;
            case nfs:
                Assert.hasText(nfsServer, "nfs server address is null");
                return String.format("%s:%s", nfsServer, ApplicationConstant.STORAGE_VOLUME_PATH);
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
