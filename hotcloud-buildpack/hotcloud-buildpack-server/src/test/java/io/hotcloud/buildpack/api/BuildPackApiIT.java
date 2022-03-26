package io.hotcloud.buildpack.api;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.common.Base64Helper;
import io.hotcloud.kubernetes.api.NamespaceApi;
import io.hotcloud.kubernetes.api.configurations.SecretApi;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class BuildPackApiIT extends BuildPackIntegrationTestBase {

    @Autowired
    private BuildPackApi buildPackApi;
    @Autowired
    private KubectlApi kubectlApi;
    public final String namespace = NamespaceGenerator.uuidNoDashNamespace();
    @Autowired
    private NamespaceApi namespaceApi;
    @Autowired
    private SecretApi secretApi;

    @Before
    public void before() throws ApiException {
        namespaceApi.namespace(namespace);
    }

    @After
    public void after() throws ApiException {
        namespaceApi.delete(namespace);
    }

    @Test
    public void storageResourceList() {
        StorageResourceList storageResourceList = buildPackApi.storageResourceList(namespace, null, null, null);

        String yaml = storageResourceList.getResourceListYaml();
        Assertions.assertTrue(StringUtils.hasText(yaml));
        log.info("storage resource list yaml: \n {}", storageResourceList.getResourceListYaml());

        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml);
        Assertions.assertFalse(CollectionUtils.isEmpty(hasMetadata));
        Assertions.assertEquals(2, hasMetadata.size());

        Boolean delete = kubectlApi.delete(namespace, yaml);
        Assertions.assertTrue(delete);
    }

    @Test
    public void secretResource() {
        SecretResource secretResource = buildPackApi.dockersecret(
                namespace,
                null,
                "index.docker.io",
                "username",
                "password");

        String yaml = secretResource.getSecretResourceYaml();
        Assertions.assertTrue(StringUtils.hasText(yaml));
        log.info("docker secret resource yaml: \n {}", yaml);

        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml);
        Assertions.assertEquals(1, hasMetadata.size());

        SecretList secretList = secretApi.read(namespace, secretResource.getLabels());
        Assertions.assertEquals(1, secretList.getItems().size());

        Secret secret = secretList.getItems().get(0);
        String auth = secret.getData().get(".dockerconfigjson");
        String expected = Base64Helper.dockerconfigjson("index.docker.io", "username", "password");
        Assertions.assertEquals(expected, Base64Helper.decode(auth));

        Boolean delete = kubectlApi.delete(namespace, yaml);
        Assertions.assertTrue(delete);
    }
}
