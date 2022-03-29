package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.*;
import io.hotcloud.common.Assert;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {


    @Override
    public BuildPack buildpack(String namespace, String gitUrl, String local, String registry, String registryUser, String registryPass) {

        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(local, "local path is null", 400);
        Assert.hasText(registry, "registry is null", 400);
        Assert.hasText(registryUser, "registry user is null", 400);
        Assert.hasText(registryPass, "registry password is null", 400);

        BuildPackRepositoryCloneRequest repository = BuildPackRepositoryCloneRequest.builder()
                .remote(gitUrl)
                .local(local)
                .build();
        BuildPackRepositoryCloned cloned = clone(repository);

        BuildPackDockerSecretResourceRequest dockersecret = BuildPackDockerSecretResourceRequest.builder()
                .namespace(namespace)
                .registry(registry)
                .username(registryUser)
                .password(registryPass)
                .build();
        BuildPackDockerSecretResource dockerSecretResource = dockersecret(dockersecret);

        BuildPackStorageResourceRequest storageResourceRequest = BuildPackStorageResourceRequest.builder()
                .namespace(namespace)
                .build();
        BuildPackStorageResourceList storageResourceList = storageResourceList(storageResourceRequest);

        BuildPackJobResourceRequest jobResourceRequest = BuildPackJobResourceRequest.builder()
                .namespace(namespace)
                .persistentVolumeClaim(storageResourceList.getPersistentVolumeClaim())
                .secret(dockerSecretResource.getName())
                .build();
        BuildPackJobResource jobResource = jobResource(jobResourceRequest);


        return DefaultBuildPack.builder()
                .storage(storageResourceList)
                .dockerSecret(dockerSecretResource)
                .job(jobResource)
                .repository(cloned)
                .build();
    }

    abstract protected String yaml(BuildPack buildPack);

    /**
     * Repository clone
     *
     * @param clone {@link  BuildPackRepositoryCloneRequest}
     * @return {@link BuildPackRepositoryCloned}
     */
    abstract protected BuildPackRepositoryCloned clone(BuildPackRepositoryCloneRequest clone);

    /**
     * Generate job Yaml resource.
     *
     * @param jobResource {@link  BuildPackJobResourceRequest}
     * @return {@link BuildPackJobResource}
     */
    abstract protected BuildPackJobResource jobResource(BuildPackJobResourceRequest jobResource);


    /**
     * Generate pv/pvc Yaml resource list.
     *
     * @param storageResource {@link BuildPackStorageResourceRequest}
     * @return {@link BuildPackStorageResourceList}
     */
    abstract protected BuildPackStorageResourceList storageResourceList(BuildPackStorageResourceRequest storageResource);

    /**
     * Generate secret Yaml resource.
     *
     * @param dockersecretResource {@link BuildPackDockerSecretResourceRequest}
     * @return {@link  BuildPackDockerSecretResource}
     */
    abstract protected BuildPackDockerSecretResource dockersecret(BuildPackDockerSecretResourceRequest dockersecretResource);
}
