package io.hotcloud.server.minio;

import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.vendor.minio.MinioBucketApi;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class MinioBucketOperator implements MinioBucketApi {

    private final MinioClient minioClient;

    public MinioBucketOperator(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void make(String bucket) {
        try {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build();
            minioClient.makeBucket(makeBucketArgs);
        } catch (Exception ex) {
            throw new NGCCAPlatformException("make bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public void remove(String bucket) {
        try {
            RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder()
                    .bucket(bucket)
                    .build();
            minioClient.removeBucket(removeBucketArgs);
        } catch (Exception ex) {
            throw new NGCCAPlatformException("remove bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public boolean exist(String bucket) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            return minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception ex) {
            throw new NGCCAPlatformException("exist bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public void setGetObjectPolicy(String bucket) {
        try {
            InputStream inputStream = new ClassPathResource("minio-GetObject-policy.template").getInputStream();
            String template = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
            String policyConfig = new SpelExpressionParser()
                    .parseExpression(template, new TemplateParserContext())
                    .getValue(Map.of("BUCKET", bucket), String.class);
            minioClient.setBucketPolicy(SetBucketPolicyArgs
                    .builder().bucket(bucket)
                    .config(policyConfig)
                    .build()
            );
        } catch (Exception e) {
            throw new NGCCAPlatformException("Set bucket policy failed: " + e.getMessage());
        }
    }
}
