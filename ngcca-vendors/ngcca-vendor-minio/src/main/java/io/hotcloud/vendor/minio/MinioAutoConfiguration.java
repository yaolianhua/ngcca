package io.hotcloud.vendor.minio;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static io.hotcloud.common.model.CommonConstant.CONFIG_PREFIX;

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = CONFIG_PREFIX + "minio", value = "endpoint")
    public MinioClient minioClient(MinioProperties properties) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        checkDefaultBucketOrCreate(minioClient, properties.getDefaultBucket());

        Log.info(this, properties, Event.START, "load minio properties");
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
