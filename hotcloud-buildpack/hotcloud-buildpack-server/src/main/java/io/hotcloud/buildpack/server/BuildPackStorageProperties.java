package io.hotcloud.buildpack.server;

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
     * PV type
     */
    private Type type = Type.hostPath;
    /**
     * Storage capacity will be allocated. default 500M
     */
    private String capacity = "500M";
    /**
     * PV type of nfs
     */
    private Nfs nfs = new Nfs();
    /**
     * PV type of hostPath
     */
    private HostPath hostPath = new HostPath();
    /**
     * Global storageClass of buildpack
     */
    private StorageClass storageClass = new StorageClass();

    @PostConstruct
    public void print() {
        log.info("【Load BuildPack Storage Properties】 storage-class = '{}', type = '{}', mount path = '{}', size = '{}'",
                storageClass.getName(),
                type,
                retrieveStoragePath(),
                capacity);
    }

    public String retrieveStoragePath() {
        switch (type) {
            case hostPath:
                return this.hostPath.getPath();
            case nfs:
                return String.format("%s:%s", this.nfs.server, this.nfs.getPath());
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

    @Data
    public static class Nfs {
        private String server = "127.0.0.1";
        private String path = "/tmp/kaniko";
    }

    @Data
    public static class HostPath {
        private String path = "/tmp/kaniko";
    }

    @Data
    public static class StorageClass {
        private String name = "storage-class-buildpack";
    }
}
