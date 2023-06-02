package io.hotcloud.vendor.registry;

import io.hotcloud.vendor.registry.model.RegistryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

 class RegistryUtilTest {

     @Test
     void retrieveRepositoryNamespace() {
         Assertions.assertEquals("library", RegistryUtil.getNamespace("nginx"));
         Assertions.assertEquals("bitnami", RegistryUtil.getNamespace("bitnami/nginx"));
         Assertions.assertEquals("cloudtogo", RegistryUtil.getNamespace("cloudtogo/factory/nginx"));
     }

     @Test
     void retrieveRepositoryNameWithNoNamespace() {
         Assertions.assertEquals("nginx", RegistryUtil.getImageNameOnly("nginx"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageNameOnly("bitnami/nginx"));
         Assertions.assertEquals("factory/nginx", RegistryUtil.getImageNameOnly("cloudtogo/factory/nginx"));
     }

     @Test
     void retrieveRepositoryNameWithNamespace() {
         Assertions.assertEquals("library/nginx", RegistryUtil.getNamespacedImage("nginx"));
         Assertions.assertEquals("bitnami/nginx", RegistryUtil.getNamespacedImage("bitnami/nginx"));
         Assertions.assertEquals("cloudtogo/factory/nginx", RegistryUtil.getNamespacedImage("cloudtogo/factory/nginx"));
     }
}
