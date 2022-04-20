package io.hotcloud.common.file.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class MinioBucketApiIT extends MinioIT {

    @Autowired
    private MinioBucketApi minioBucketApi;

    @Test
    public void make_exist_removed() {
        minioBucketApi.make("example-bucket");
        boolean exist = minioBucketApi.exist("example-bucket");
        Assertions.assertTrue(exist);

        minioBucketApi.remove("example-bucket");
    }
}
