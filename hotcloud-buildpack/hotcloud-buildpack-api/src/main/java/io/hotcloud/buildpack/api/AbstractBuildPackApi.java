package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.*;
import io.hotcloud.common.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {


    @Override
    public BuildPack buildpack(String namespace, String gitUrl, String clonePath, boolean force, boolean async, String registry, String registryUser, String registryPass, Map<String, String> kanikoArgs) {

        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(gitUrl, "git url is null", 400);
        Assert.hasText(clonePath, "clone path is null", 400);
        Assert.hasText(registry, "registry is null", 400);
        Assert.hasText(registryUser, "registry user is null", 400);
        Assert.hasText(registryPass, "registry password is null", 400);
        Assert.state(!CollectionUtils.isEmpty(kanikoArgs), "kaniko args is empty", 400);

        //Repository clone
        BuildPackRepositoryCloneInternalInput repository = BuildPackRepositoryCloneInternalInput.builder()
                .remote(gitUrl)
                .local(clonePath)
                .force(force)
                .async(async)
                .build();
        BuildPackRepositoryCloned cloned = clone(repository);
        Assert.notNull(cloned, "BuildPack Repository clone failed", 404);

        Map<String, String> alternative = new HashMap<>(16);
        alternative.put(BuildPackConstant.GIT_PROJECT_NAME, cloned.getProject());
        alternative.put(BuildPackConstant.GIT_PROJECT_PATH, clonePath);

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
        DefaultBuildPack buildPack = DefaultBuildPack.builder()
                .storage(storageResourceList)
                .dockerSecret(dockerSecretResource)
                .job(jobResource)
                .repository(cloned)
                .build();

        buildPack.setBuildPackYaml(yaml(buildPack));
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
     * Repository clone
     *
     * @param clone {@link  BuildPackRepositoryCloneInternalInput}
     * @return {@link BuildPackRepositoryCloned}
     */
    abstract protected BuildPackRepositoryCloned clone(BuildPackRepositoryCloneInternalInput clone);

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
