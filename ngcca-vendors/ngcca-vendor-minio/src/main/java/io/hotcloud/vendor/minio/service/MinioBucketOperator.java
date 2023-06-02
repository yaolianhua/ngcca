package io.hotcloud.vendor.minio.service;

import io.hotcloud.common.model.exception.PlatformException;
import io.minio.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class MinioBucketOperator implements MinioBucketApi {

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
            throw new PlatformException("make bucket failed: " + ex.getMessage());
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
            throw new PlatformException("remove bucket failed: " + ex.getMessage());
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
            throw new PlatformException("exist bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public void setGetObjectPolicy(String bucket) {
        try {
            InputStream inputStream = new ClassPathResource("allow-policy.json").getInputStream();
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
            throw new PlatformException("Set bucket policy failed: " + e.getMessage());
        }
    }
}
