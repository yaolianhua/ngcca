package io.hotcloud.common.server.core.minio;

import io.hotcloud.common.api.core.minio.MinioProperties;
import io.hotcloud.common.model.Log;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnProperty(prefix = "minio", value = "endpoint")
public class MinioConfiguration {

    private final MinioProperties properties;

    public MinioConfiguration(MinioProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        Log.info(MinioConfiguration.class.getName(),
                String.format("【Load Minio Configuration. endpoint = '%s' default-bucket = '%s' max-upload-megabytes = '%sMB'】",
                        properties.getEndpoint(), properties.getDefaultBucket(), properties.getMaxUploadMegabytes()));
    }

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        checkDefaultBucketOrCreate(minioClient, properties.getDefaultBucket());
        return minioClient;
    }

    private void checkDefaultBucketOrCreate(MinioClient client, String bucket) throws Exception {
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (bucketExists) {
            return;
        }
        client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        log.info("Created default-bucket '{}'", bucket);
    }
}
