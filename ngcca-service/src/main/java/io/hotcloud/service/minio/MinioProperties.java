package io.hotcloud.service.minio;

import io.hotcloud.common.model.Properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = CONFIG_PREFIX + "minio")
@Properties(prefix = CONFIG_PREFIX + "minio")
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
