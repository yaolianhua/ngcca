package io.hotcloud.buildpack.server;

import io.hotcloud.buildpack.api.KanikoFlag;
import io.hotcloud.common.HotCloudException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "buildpack.kaniko")
@Data
public class KanikoFlagProperties implements KanikoFlag {

    private boolean cache = false;
    private boolean cleanup = true;
    private boolean compressedCaching = true;
    private boolean force = false;
    private boolean insecure = true;
    private boolean insecurePull = true;
    private boolean logTimestamp = false;
    private boolean noPush = true;
    private boolean reproducible = false;
    private boolean singleSnapshot = false;
    private boolean skipTlsVerify = true;
    private boolean skipTlsVerifyPull = true;
    private boolean skipTlsVerifyRegistry = true;
    private boolean skipUnusedStages = false;
    private boolean useNewRun = true;
    private boolean ignoreVarRun = true;

    private int pushRetry = 3;

    private String context = "dir://workspace";
    private String destination = "index.docker.io/username/";
    private String registryMirror;
    private String registryCertificate;
    private String cacheDir;
    private String cacheRepo;
    private String cacheTtlDuration = "168h";
    private String contextSubPath;
    private String digestFile;
    private String dockerfile = "/workspace/Dockerfile";
    private String git;
    private String imageNameWithDigestFile;
    private String imageNameTagWithDigestFile;
    private String insecureRegistry = "index.docker.io";
    private String label;
    private String logFormat = "color";
    private String snapshotMode = "full";
    private String tarPath = "/workspace";
    private String target;
    private String verbosity = "debug";
    private String ignorePath;
    private int imageFsExtractRetry = 3;

    @Override
    public Map<String, String> resolvedArgs() {
        Field[] declaredFields = KanikoFlagProperties.class.getDeclaredFields();
        Map<String, String> args = new HashMap<>(64);
        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                Object o = field.get(this);
                if (o == null) {
                    continue;
                }
                if (o instanceof String && !StringUtils.hasText(((String) o))) {
                    continue;
                }
                args.put(field.getName(), String.valueOf(o));
            } catch (IllegalAccessException e) {
                throw new HotCloudException(e.getMessage(), e);
            }
        }

        return args;
    }

}
