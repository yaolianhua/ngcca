package io.hotcloud.server.buildpack;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.client.http.SecretClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.*;
import io.hotcloud.server.NgccaCoreServerApplication;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaCoreServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class BuildPackApiIT {

    @Autowired
    private AbstractBuildPackApi buildPackApi;
    @Autowired
    private KubectlClient kubectlApi;
    @Autowired
    private NamespaceClient namespaceApi;
    @Autowired
    private SecretClient secretApi;

    public final String namespace = UUIDGenerator.uuidNoDash();
    @Before
    public void before() throws ApiException {
        namespaceApi.create(namespace);
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

        BuildPackJobResourceInternalInput jobResource = BuildPackJobResourceInternalInput.builder()
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
        BuildPackStorageResourceInternalInput resource = BuildPackStorageResourceInternalInput.builder()
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

        List<HasMetadata> hasMetadata = kubectlApi.resourceListCreateOrReplace(namespace, YamlBody.of(yaml));
        Assertions.assertFalse(CollectionUtils.isEmpty(hasMetadata));
        Assertions.assertEquals(2, hasMetadata.size());

        Boolean delete = kubectlApi.delete(namespace, YamlBody.of(yaml));
        Assertions.assertTrue(delete);
    }

    @Test
    public void secretResource() {
        BuildPackDockerSecretResourceInternalInput dockersecret = BuildPackDockerSecretResourceInternalInput.builder()
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

        List<HasMetadata> hasMetadata = kubectlApi.resourceListCreateOrReplace(namespace, YamlBody.of(yaml));
        Assertions.assertEquals(1, hasMetadata.size());

        SecretList secretList = secretApi.readList(namespace, buildPackDockerSecretResource.getLabels());
        Assertions.assertEquals(1, secretList.getItems().size());

        Secret secret = secretList.getItems().get(0);
        String auth = secret.getData().get(".dockerconfigjson");
        String dockerconfigjson = new String(Base64.getDecoder().decode(auth), StandardCharsets.UTF_8);

        Assertions.assertEquals(dockersecret.dockerconfigjson(), dockerconfigjson);

        Boolean delete = kubectlApi.delete(namespace, YamlBody.of(yaml));
        Assertions.assertTrue(delete);
    }
}
