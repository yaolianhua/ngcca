package io.hotcloud.server.support.buildpack;

import io.hotcloud.support.kaniko.KanikoFlag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "buildpack.kaniko")
@Data
@Slf4j
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
    private String logFormat = "color";
    private String snapshotMode = "full";
    private String tarPath;
    private String target;
    private String verbosity = "debug";
    private String ignorePath;
    private int imageFsExtractRetry = 3;

    @PostConstruct
    public void print() throws IllegalAccessException {
        Field[] declaredFields = KanikoFlagProperties.class.getDeclaredFields();
        StringBuilder args = new StringBuilder();
        args.append("[\n");
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(this);
            if ("log".equals(field.getName())) {
                continue;
            }
            if (o instanceof String && !StringUtils.hasText(((String) o))) {
                continue;
            }

            args.append("\t").append("--").append(field.getName()).append("=").append(o);
            args.append("\n");
        }
        args.append("]");

        log.info("【Load Kaniko Flag Configuration】\n {}", args.toString());
    }
}
