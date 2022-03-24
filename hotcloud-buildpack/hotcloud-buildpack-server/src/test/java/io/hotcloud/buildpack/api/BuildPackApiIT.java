package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class BuildPackApiIT extends BuildPackIntegrationTestBase {

    @Autowired
    private BuildPackApi buildPackApi;

    @Test
    public void storageResourceList() {
        StorageResourceList storageResourceList = buildPackApi.storageResourceList();
        log.info("resource list: \n {}", storageResourceList);
    }
}
