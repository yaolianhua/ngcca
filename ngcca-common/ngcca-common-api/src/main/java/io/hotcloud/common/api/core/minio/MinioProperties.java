package io.hotcloud.common.api.core.minio;

import io.hotcloud.common.api.env.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
@Slf4j
@Properties(prefix = "minio")
public class MinioProperties {

    /**
     * Access key for minio
     */
    private String accessKey;

    /**
     * Secret key for minio
     */
    private String secretKey;

    /**
     * Minio endpoint url
     */
    private String endpoint;

    /**
     * Minio default bucket
     */
    private String defaultBucket = "default";

    /**
     * File upload max megabytes
     */
    private Integer maxUploadMegabytes = 500;
}
