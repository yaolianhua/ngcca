package io.hotcloud.vendor.registry;

import io.hotcloud.vendor.registry.model.RegistryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

 class RegistryUtilTest {

     @Test
     void getNamespace() {
         Assertions.assertEquals("library", RegistryUtil.getNamespace("nginx"));
         Assertions.assertEquals("library", RegistryUtil.getNamespace("nginx:latest"));
         Assertions.assertEquals("bitnami", RegistryUtil.getNamespace("bitnami/nginx"));
         Assertions.assertEquals("factory", RegistryUtil.getNamespace("cloud/factory/nginx"));
         Assertions.assertEquals("cloud", RegistryUtil.getNamespace("harbor.local:5000/cloud/factory/nginx"));
         Assertions.assertEquals("cloud", RegistryUtil.getNamespace("harbor.local:5000/cloud/factory/nginx:1.21.5"));
         Assertions.assertEquals("library", RegistryUtil.getNamespace("docker.io/library/nginx"));
         Assertions.assertEquals("bitnami", RegistryUtil.getNamespace("docker.io/bitnami/nginx:latest"));
     }

     @Test
     void getRegistry() {
         Assertions.assertEquals("docker.io", RegistryUtil.getRegistry("nginx"));
         Assertions.assertEquals("docker.io", RegistryUtil.getRegistry("nginx:latest"));
         Assertions.assertEquals("docker.io", RegistryUtil.getRegistry("bitnami/nginx"));
         Assertions.assertEquals("cloud", RegistryUtil.getRegistry("cloud/factory/nginx"));
         Assertions.assertEquals("harbor.local:5000", RegistryUtil.getRegistry("harbor.local:5000/cloud/factory/nginx"));
         Assertions.assertEquals("harbor.local:5000", RegistryUtil.getRegistry("harbor.local:5000/cloud/factory/nginx:1.21.5"));
         Assertions.assertEquals("docker.io", RegistryUtil.getRegistry("docker.io/library/nginx"));
         Assertions.assertEquals("docker.io", RegistryUtil.getRegistry("docker.io/bitnami/nginx:latest"));
     }

     @Test
     void getImageName() {
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("nginx"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("nginx:latest"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("bitnami/nginx"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("cloud/factory/nginx"));
         Assertions.assertEquals("factory/nginx", RegistryUtil.getImageName("harbor.local:5000/cloud/factory/nginx"));
         Assertions.assertEquals("factory/nginx", RegistryUtil.getImageName("harbor.local:5000/cloud/factory/nginx:1.21.5"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("docker.io/library/nginx"));
         Assertions.assertEquals("nginx", RegistryUtil.getImageName("docker.io/bitnami/nginx:latest"));
     }

     @Test
     void getImageTag() {
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("nginx"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("nginx:latest"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("bitnami/nginx"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("cloud/factory/nginx"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("harbor.local:5000/cloud/factory/nginx"));
         Assertions.assertEquals("1.21.5", RegistryUtil.getImageTag("harbor.local:5000/cloud/factory/nginx:1.21.5"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("docker.io/library/nginx"));
         Assertions.assertEquals("latest", RegistryUtil.getImageTag("docker.io/bitnami/nginx:latest"));
     }

     @Test
     void getNamespacedImage() {
         Assertions.assertEquals("library/nginx", RegistryUtil.getNamespacedImageName("nginx"));
         Assertions.assertEquals("bitnami/nginx", RegistryUtil.getNamespacedImageName("bitnami/nginx"));
         Assertions.assertEquals("factory/nginx", RegistryUtil.getNamespacedImageName("cloud/factory/nginx"));

         Assertions.assertEquals("cloud/factory/nginx", RegistryUtil.getNamespacedImageName("harbor.local:5000/cloud/factory/nginx"));
         Assertions.assertEquals("cloud/factory/nginx", RegistryUtil.getNamespacedImageName("harbor.local:5000/cloud/factory/nginx:1.21.5"));
         Assertions.assertEquals("library/nginx", RegistryUtil.getNamespacedImageName("docker.io/library/nginx"));
         Assertions.assertEquals("bitnami/nginx", RegistryUtil.getNamespacedImageName("docker.io/bitnami/nginx:latest"));
     }

     @Test
     void resolvedName() {
         Assertions.assertEquals("docker.io/library/nginx:latest", RegistryUtil.resolvedName("nginx"));
         Assertions.assertEquals("docker.io/library/nginx:latest", RegistryUtil.resolvedName("nginx:latest"));
         Assertions.assertEquals("docker.io/library/nginx:1.21.5", RegistryUtil.resolvedName("nginx:1.21.5"));

         Assertions.assertEquals("docker.io/bitnami/nginx:latest", RegistryUtil.resolvedName("bitnami/nginx"));
         Assertions.assertEquals("docker.io/bitnami/nginx:1.21.5", RegistryUtil.resolvedName("bitnami/nginx:1.21.5"));

         Assertions.assertEquals("docker.io/library/nginx:latest", RegistryUtil.resolvedName("docker.io/library/nginx"));
         Assertions.assertEquals("docker.io/bitnami/nginx:latest", RegistryUtil.resolvedName("docker.io/bitnami/nginx:latest"));

         //
         Assertions.assertEquals("cloud/factory/nginx:latest", RegistryUtil.resolvedName("cloud/factory/nginx"));
         Assertions.assertEquals("cloud/factory/nginx:1.21.5", RegistryUtil.resolvedName("cloud/factory/nginx:1.21.5"));


         Assertions.assertEquals("harbor.local:5000/cloud/library/nginx:latest", RegistryUtil.resolvedName("harbor.local:5000/cloud/library/nginx"));
         Assertions.assertEquals("harbor.local:5000/library/nginx:latest", RegistryUtil.resolvedName("harbor.local:5000/library/nginx"));
         Assertions.assertEquals("harbor.local:5000/cloud/library/nginx:1.21.5", RegistryUtil.resolvedName("harbor.local:5000/cloud/library/nginx:1.21.5"));


     }
 }
