package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.*;

import javax.annotation.Nullable;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {

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
     * @param namespace        In which namespace the secret will be created
     * @param name             The name secret will be created
     * @param registry         The registry address e.g. index.docker.io
     * @param registryUsername Registry username e.g. your docker hub username
     * @param registryPassword Registry password e.g. your docker hub password
     * @return {@link  BuildPackSecretResource}
     */
    abstract protected BuildPackSecretResource dockersecret(String namespace, @Nullable String name, String registry, String registryUsername, String registryPassword);
}
