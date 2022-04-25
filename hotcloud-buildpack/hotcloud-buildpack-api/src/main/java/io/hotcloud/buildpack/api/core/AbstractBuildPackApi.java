package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.api.core.model.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {


    @Override
    public BuildPack buildpack(String namespace, String gitProject, String registry, String registryUser, String registryPass, Map<String, String> kanikoArgs) {

        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(gitProject, "git project name is null");
        Assert.hasText(registry, "registry is null");
        Assert.hasText(registryUser, "registry credential user is null");
        Assert.hasText(registryPass, "registry credential password is null");
        Assert.state(!CollectionUtils.isEmpty(kanikoArgs), "kaniko args is empty");

        Map<String, String> alternative = new HashMap<>(16);
        alternative.put(BuildPackConstant.GIT_PROJECT_NAME, gitProject);
        alternative.put(BuildPackConstant.GIT_PROJECT_PATH, Path.of(BuildPackConstant.STORAGE_VOLUME_PATH, namespace, gitProject).toString());

        //Docker secret auth
        BuildPackDockerSecretResourceInternalInput dockersecret = BuildPackDockerSecretResourceInternalInput.builder()
                .namespace(namespace)
                .registry(registry)
                .username(registryUser)
                .password(registryPass)
                .alternative(alternative)
                .build();
        BuildPackDockerSecretResource dockerSecretResource = dockersecret(dockersecret);

        //persistentVolume & persistentVolumeClaim
        BuildPackStorageResourceInternalInput storageResourceRequest = BuildPackStorageResourceInternalInput.builder()
                .namespace(namespace)
                .alternative(alternative)
                .build();
        BuildPackStorageResourceList storageResourceList = storageResourceList(storageResourceRequest);

        //Kaniko job
        BuildPackJobResourceInternalInput jobResourceRequest = BuildPackJobResourceInternalInput.builder()
                .namespace(namespace)
                .persistentVolumeClaim(storageResourceList.getPersistentVolumeClaim())
                .secret(dockerSecretResource.getName())
                .args(kanikoArgs)
                .alternative(alternative)
                .build();
        BuildPackJobResource jobResource = jobResource(jobResourceRequest);

        //Build final deployment yaml
        BuildPack buildPack = BuildPack.builder()
                .storageResource(storageResourceList)
                .secretResource(dockerSecretResource)
                .jobResource(jobResource)
                .build();

        buildPack.setYaml(yaml(buildPack));
        return buildPack;
    }

    /**
     * Generate final buildpack yaml from input {@link BuildPack}
     *
     * @param buildPack {@link BuildPack}
     * @return Publishable yaml resource
     */
    abstract protected String yaml(BuildPack buildPack);

    /**
     * Generate job Yaml resource.
     *
     * @param jobResource {@link  BuildPackJobResourceInternalInput}
     * @return {@link BuildPackJobResource}
     */
    abstract protected BuildPackJobResource jobResource(BuildPackJobResourceInternalInput jobResource);


    /**
     * Generate pv/pvc Yaml resource list.
     *
     * @param storageResource {@link BuildPackStorageResourceInternalInput}
     * @return {@link BuildPackStorageResourceList}
     */
    abstract protected BuildPackStorageResourceList storageResourceList(BuildPackStorageResourceInternalInput storageResource);

    /**
     * Generate secret Yaml resource.
     *
     * @param dockersecretResource {@link BuildPackDockerSecretResourceInternalInput}
     * @return {@link  BuildPackDockerSecretResource}
     */
    abstract protected BuildPackDockerSecretResource dockersecret(BuildPackDockerSecretResourceInternalInput dockersecretResource);
}
