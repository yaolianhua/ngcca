package io.hotcloud.buildpack.api;

import io.hotcloud.buildpack.api.model.*;

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
     * @param dockersecretResource {@link BuildPackDockerSecretResourceRequest}
     * @return {@link  BuildPackDockerSecretResource}
     */
    abstract protected BuildPackDockerSecretResource dockersecret(BuildPackDockerSecretResourceRequest dockersecretResource);
}
