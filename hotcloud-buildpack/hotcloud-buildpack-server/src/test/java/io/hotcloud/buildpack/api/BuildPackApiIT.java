package io.hotcloud.buildpack.api;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class BuildPackApiIT extends BuildPackIntegrationTestBase {

    @Autowired
    private BuildPackApi buildPackApi;
    @Autowired
    private KubectlApi kubectlApi;

    @Test
    public void storageResourceList() {
        String namespace = NamespaceGenerator.uuidNoDashNamespace("buildpack");
        StorageResourceList storageResourceList = buildPackApi.storageResourceList(namespace);
        log.info("resource list yaml: \n {}", storageResourceList.getResourceListYaml());

        String yaml = storageResourceList.getResourceListYaml();
        Assertions.assertTrue(StringUtils.hasText(yaml));

        List<HasMetadata> hasMetadata = kubectlApi.apply(null, yaml);
        Assertions.assertFalse(CollectionUtils.isEmpty(hasMetadata));
        Assertions.assertEquals(3, hasMetadata.size());

        Boolean delete = kubectlApi.delete(null, yaml);
        Assertions.assertTrue(delete);
    }
}
