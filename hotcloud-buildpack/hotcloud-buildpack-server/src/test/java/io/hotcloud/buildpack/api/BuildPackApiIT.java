package io.hotcloud.buildpack.api;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.api.model.*;
import io.hotcloud.common.Base64Helper;
import io.hotcloud.kubernetes.api.configurations.SecretApi;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class BuildPackApiIT extends BuildPackIntegrationTestBase {

    @Autowired
    private AbstractBuildPackApi buildPackApi;
    @Autowired
    private KubectlApi kubectlApi;
    @Autowired
    private NamespaceApi namespaceApi;
    @Autowired
    private SecretApi secretApi;

    public final String namespace = NamespaceGenerator.uuidNoDashNamespace();
    @Before
    public void before() throws ApiException {
        namespaceApi.namespace(namespace);
    }

    @After
    public void after() throws ApiException {
        namespaceApi.delete(namespace);
    }

    @Test
    public void jobResource() {
        Map<String, String> args = Map.of(
                "insecure-registry", "docker-registry-idc01-sz.cloudtogo.cn",
                "destination", "docker-registry-idc01-sz.cloudtogo.cn/cloudtogo/devops-thymeleaf:0.3",
                "tarPath", "/workspace/devops.tar");

        BuildPackJobResourceRequest jobResource = BuildPackJobResourceRequest.builder()
                .namespace(namespace)
                .persistentVolumeClaim("pvc-" + namespace)
                .secret("secret-" + namespace)
                .args(args)
                .build();
        BuildPackJobResource buildPackJobResource = buildPackApi.jobResource(jobResource);
        Assertions.assertNotNull(buildPackJobResource.getJobResourceYaml());
        Assertions.assertEquals(namespace, buildPackJobResource.getNamespace());
        log.info("job yaml \n {}", buildPackJobResource.getJobResourceYaml());
    }

    @Test
    public void storageResourceList() {
        BuildPackStorageResourceRequest resource = BuildPackStorageResourceRequest.builder()
                .namespace(namespace)
                .capacity("1Gi")
                .persistentVolume(null)
                .persistentVolumeClaim(null)
                .alternative(Map.of(BuildPackConstant.GIT_PROJECT_PATH, "/tmp/kaniko/namespace/project"))
                .build();
        BuildPackStorageResourceList buildPackStorageResourceList = buildPackApi.storageResourceList(resource);

        String yaml = buildPackStorageResourceList.getResourceListYaml();
        Assertions.assertTrue(StringUtils.hasText(yaml));
        log.info("storage resource list yaml: \n {}", buildPackStorageResourceList.getResourceListYaml());

        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml);
        Assertions.assertFalse(CollectionUtils.isEmpty(hasMetadata));
        Assertions.assertEquals(2, hasMetadata.size());

        Boolean delete = kubectlApi.delete(namespace, yaml);
        Assertions.assertTrue(delete);
    }

    @Test
    public void secretResource() {
        BuildPackDockerSecretResourceRequest dockersecret = BuildPackDockerSecretResourceRequest.builder()
                .name(null)
                .namespace(namespace)
                .registry("index.docker.io")
                .username("username")
                .password("password")
                .build();
        BuildPackDockerSecretResource buildPackDockerSecretResource = buildPackApi.dockersecret(dockersecret);

        String yaml = buildPackDockerSecretResource.getSecretResourceYaml();
        Assertions.assertTrue(StringUtils.hasText(yaml));
        log.info("docker secret resource yaml: \n {}", yaml);

        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml);
        Assertions.assertEquals(1, hasMetadata.size());

        SecretList secretList = secretApi.read(namespace, buildPackDockerSecretResource.getLabels());
        Assertions.assertEquals(1, secretList.getItems().size());

        Secret secret = secretList.getItems().get(0);
        String auth = secret.getData().get(".dockerconfigjson");
        String expected = Base64Helper.dockerconfigjson("https://index.docker.io/v1/", "username", "password");
        Assertions.assertEquals(expected, Base64Helper.decode(auth));

        Boolean delete = kubectlApi.delete(namespace, yaml);
        Assertions.assertTrue(delete);
    }
}
