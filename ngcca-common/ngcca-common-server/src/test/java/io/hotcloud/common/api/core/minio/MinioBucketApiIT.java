package io.hotcloud.common.api.core.minio;

import io.hotcloud.common.NgccaCommonApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgccaCommonApplicationTest.class
)
@ActiveProfiles("test")
@Slf4j
public class MinioBucketApiIT {

    @Autowired
    private MinioBucketApi minioBucketApi;

    @Test
    public void make_exist_removed() {
        minioBucketApi.make("example-bucket");
        boolean exist = minioBucketApi.exist("example-bucket");
        Assertions.assertTrue(exist);

        minioBucketApi.remove("example-bucket");
    }

    @Test
    public void make_setPolicy_removed() {
        String bucket = "custom-policy";
        if (!minioBucketApi.exist(bucket)) {
            minioBucketApi.make(bucket);
        }

        minioBucketApi.setGetObjectPolicy(bucket);

        minioBucketApi.remove(bucket);
    }
}
