package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.api.BuildPackConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "buildpack.storage")
@Data
@Slf4j
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
        log.info("【Load BuildPack Storage Properties】type = '{}', mount path = '{}'", type, retrieveStoragePath());
    }

    public String retrieveStoragePath() {
        switch (type) {
            case hostPath:
                return BuildPackConstant.STORAGE_VOLUME_PATH;
            case nfs:
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
