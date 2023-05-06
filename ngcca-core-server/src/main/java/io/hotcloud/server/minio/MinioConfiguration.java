package io.hotcloud.server.minio;

import io.hotcloud.common.log.Log;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX + "minio", value = "endpoint")
    public MinioClient minioClient(MinioProperties properties) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        checkDefaultBucketOrCreate(minioClient, properties.getDefaultBucket());

        Log.info(MinioConfiguration.class.getName(),
                String.format("【Load Minio Configuration. endpoint = '%s' default-bucket = '%s' max-upload-megabytes = '%sMB'】",
                        properties.getEndpoint(), properties.getDefaultBucket(), properties.getMaxUploadMegabytes()));
        return minioClient;
    }

    private void checkDefaultBucketOrCreate(MinioClient client, String bucket) throws Exception {
        boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (bucketExists) {
            return;
        }
        client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }
}
