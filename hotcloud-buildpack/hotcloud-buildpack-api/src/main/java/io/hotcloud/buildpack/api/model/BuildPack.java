package io.hotcloud.buildpack.api.model;


/**
 * @author yaolianhua789@gmail.com
 **/
public interface BuildPack {

    BuildPackRepositoryCloned getRepository();

    BuildPackJobResource getJob();

    BuildPackStorageResourceList getStorage();

    BuildPackDockerSecretResource getDockerSecret();

    String getBuildPackYaml();
}
