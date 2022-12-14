package io.hotcloud.common.model.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistryUtilTest {

    @Test
    public void retrieveRepositoryNamespace() {
        Assertions.assertEquals("library", RegistryUtil.retrieveRepositoryNamespace("nginx"));
        Assertions.assertEquals("bitnami", RegistryUtil.retrieveRepositoryNamespace("bitnami/nginx"));
        Assertions.assertEquals("cloudtogo", RegistryUtil.retrieveRepositoryNamespace("cloudtogo/factory/nginx"));
    }

    @Test
    public void retrieveRepositoryNameWithNoNamespace() {
        Assertions.assertEquals("nginx", RegistryUtil.retrieveRepositoryNameWithNoNamespace("nginx"));
        Assertions.assertEquals("nginx", RegistryUtil.retrieveRepositoryNameWithNoNamespace("bitnami/nginx"));
        Assertions.assertEquals("factory/nginx", RegistryUtil.retrieveRepositoryNameWithNoNamespace("cloudtogo/factory/nginx"));
    }

    @Test
    public void retrieveRepositoryNameWithNamespace() {
        Assertions.assertEquals("library/nginx", RegistryUtil.retrieveRepositoryNameWithNamespace("nginx"));
        Assertions.assertEquals("bitnami/nginx", RegistryUtil.retrieveRepositoryNameWithNamespace("bitnami/nginx"));
        Assertions.assertEquals("cloudtogo/factory/nginx", RegistryUtil.retrieveRepositoryNameWithNamespace("cloudtogo/factory/nginx"));
    }
}
