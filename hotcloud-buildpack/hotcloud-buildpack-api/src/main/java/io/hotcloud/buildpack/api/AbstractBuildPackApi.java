package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.*;
import io.hotcloud.common.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {


    @Override
    public BuildPack buildpack(String namespace, String gitUrl, String clonePath, boolean force, String registry, String registryUser, String registryPass, Map<String, String> kanikoArgs) {

        Assert.hasText(namespace, "namespace is null", 400);
        Assert.hasText(gitUrl, "git url is null", 400);
        Assert.hasText(clonePath, "clone path is null", 400);
        Assert.hasText(registry, "registry is null", 400);
        Assert.hasText(registryUser, "registry user is null", 400);
        Assert.hasText(registryPass, "registry password is null", 400);
        Assert.state(!CollectionUtils.isEmpty(kanikoArgs), "kaniko args is empty", 400);

        BuildPackRepositoryCloneRequest repository = BuildPackRepositoryCloneRequest.builder()
                .remote(gitUrl)
                .local(clonePath)
                .force(force)
                .build();
        BuildPackRepositoryCloned cloned = clone(repository);
        Assert.notNull(cloned, "BuildPack Repository clone failed", 404);

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
                .args(kanikoArgs)
                .build();
        BuildPackJobResource jobResource = jobResource(jobResourceRequest);


        DefaultBuildPack buildPack = DefaultBuildPack.builder()
                .storage(storageResourceList)
                .dockerSecret(dockerSecretResource)
                .job(jobResource)
                .repository(cloned)
                .build();

        buildPack.setBuildPackYaml(yaml(buildPack));
        return buildPack;
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
