package io.hotcloud.server.buildpack.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.module.buildpack.KanikoFlag;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration
@ConfigurationProperties(prefix = "buildpack.kaniko")
@Data
public class KanikoFlagProperties implements KanikoFlag {

    private boolean cache = true;
    private boolean cleanup = false;
    @JsonProperty("compressed-caching")
    private boolean compressedCaching = true;
    private boolean force = false;
    private boolean insecure = true;
    @JsonProperty("insecure-pull")
    private boolean insecurePull = true;
    @JsonProperty("log-timestamp")
    private boolean logTimestamp = false;
    @JsonProperty("no-push")
    private boolean noPush = true;
    private boolean reproducible = false;
    @JsonProperty("single-snapshot")
    private boolean singleSnapshot = false;
    @JsonProperty("skip-tls-verify")
    private boolean skipTlsVerify = true;
    @JsonProperty("skip-tls-verify-pull")
    private boolean skipTlsVerifyPull = true;
    @JsonProperty("skip-tls-verify-registry")
    private boolean skipTlsVerifyRegistry = true;
    @JsonProperty("skip-unused-stages")
    private boolean skipUnusedStages = false;
    @JsonProperty("use-new-run")
    private boolean useNewRun = true;
    @JsonProperty("ignore-var-run")
    private boolean ignoreVarRun = true;

    private String context = "/workspace";
    @JsonProperty("insecure-registry")
    private String insecureRegistry;
    private String destination;
    private String tarPath = Path.of(this.context).toString();
    @JsonProperty("cache-ttl")
    private String cacheTtl = "336h";
    @JsonProperty("cache-dir")
    private String cacheDir = "/cache";
    private String dockerfile = Path.of(this.context, "Dockerfile").toString();
    @JsonProperty("log-format")
    private String logFormat = "color";
    private String snapshotMode = "full";
    private String verbosity = "debug";
    @JsonProperty("image-fs-extract-retry")
    private int imageFsExtractRetry = 3;
    @JsonProperty("push-retry")
    private int pushRetry = 3;

    @JsonProperty("registry-mirror")
    private String registryMirror;
    @JsonProperty("registry-certificate")
    private String registryCertificate;
    @JsonProperty("cache-repo")
    private String cacheRepo;
    @JsonProperty("context-sub-path")
    private String contextSubPath;
    @JsonProperty("digest-file")
    private String digestFile;
    private String git;
    @JsonProperty("image-name-with-digest-file")
    private String imageNameWithDigestFile;
    @JsonProperty("image-name-tag-with-digest-file")
    private String imageNameTagWithDigestFile;
    private String label;
    private String target;
    @JsonProperty("ignore-path")
    private String ignorePath;


    @Override
    public Map<String, String> resolvedArgs() {
        Field[] declaredFields = KanikoFlagProperties.class.getDeclaredFields();
        Map<String, String> args = new HashMap<>(64);
        for (Field field : declaredFields) {
            try {
                Object o = field.get(this);
                if (o == null || String.valueOf(o).isBlank()) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    args.put(jsonProperty.value(), String.valueOf(o));
                    continue;
                }
                args.put(field.getName(), String.valueOf(o));
            } catch (IllegalAccessException e) {
                throw new NGCCAPlatformException(e.getMessage(), e);
            }
        }

        return args;
    }

}
