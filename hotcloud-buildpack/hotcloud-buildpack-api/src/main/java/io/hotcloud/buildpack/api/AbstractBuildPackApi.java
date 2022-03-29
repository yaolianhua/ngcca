package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.BuildPackJobResource;
import io.hotcloud.buildpack.api.model.BuildPackJobResourceRequest;
import io.hotcloud.buildpack.api.model.BuildPackSecretResource;
import io.hotcloud.buildpack.api.model.BuildPackStorageResourceList;

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
     * @param namespace In which namespace the pvc will be created
     * @param pv        The name pv will be created
     * @param pvc       The name pvc will be created
     * @param sizeGb    The capacity of pv. unit is GB
     * @return {@link BuildPackStorageResourceList}
     */
    abstract protected BuildPackStorageResourceList storageResourceList(String namespace, @Nullable String pv, @Nullable String pvc, @Nullable Integer sizeGb);

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
