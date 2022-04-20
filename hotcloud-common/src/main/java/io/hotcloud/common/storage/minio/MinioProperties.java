package io.hotcloud.common.storage.minio;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties(prefix = "minio")
@Slf4j
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

}
