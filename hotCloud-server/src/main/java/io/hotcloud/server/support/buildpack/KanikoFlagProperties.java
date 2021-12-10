package io.hotcloud.server.support.buildpack;

import io.hotcloud.support.kaniko.KanikoFlag;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "buildpack.kaniko")
@Data
@Slf4j
@ToString
public class KanikoFlagProperties implements KanikoFlag {

    private boolean cache = false;
    private boolean cleanup = true;
    private boolean compressedCaching = true;
    private boolean force = false;
    private boolean insecure = true;
    private boolean insecurePull = true;
    private boolean logTimestamp = false;
    private boolean noPush = false;
    private boolean reproducible = false;
    private boolean singleSnapshot = false;
    private boolean skipTlsVerify = true;
    private boolean skipTlsVerifyPull = true;
    private boolean skipTlsVerifyRegistry = true;
    private boolean skipUnusedStages = false;
    private boolean useNewRun = true;
    private boolean ignoreVarRun = true;

    private int pushRetry = 3;

    private String context;
    private String destination;
    private String registryMirror;
    private String registryCertificate;
    private String cacheDir;
    private String cacheRepo;
    private String cacheTtlDuration = "168h";
    private String contextSubPath;
    private String digestFile;
    private String dockerfile = "Dockerfile";
    private String git;
    private String imageNameWithDigestFile;
    private String imageNameTagWithDigestFile;
    private String insecureRegistry;
    private String label;
    private String logFormat;
    private String snapshotMode;
    private String tarPath;
    private String target;
    private String verbosity = "debug";
    private String ignorePath;
    private int imageFsExtractRetry = 3;

    @PostConstruct
    public void print() {
        log.info("{}", this);
    }
}
