package io.hotcloud.buildpack.api;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractBuildPackApi implements BuildPackApi {

    /**
     * Generate job Yaml resource.
     *
     * @param namespace In which namespace the job will be created
     * @param pvc       The pvc name that has been bound to the pv
     * @param secret    The docker secret name that has been created from your registry
     * @param args      Kaniko args mapping
     * @return {@link JobResource}
     */
    abstract protected JobResource jobResource(String namespace, String pvc, String secret, Map<String, String> args);


    /**
     * Generate pv/pvc Yaml resource list.
     *
     * @param namespace In which namespace the pvc will be created
     * @param pv        The name pv will be created
     * @param pvc       The name pvc will be created
     * @param sizeGb    The capacity of pv. unit is GB
     * @return {@link StorageResourceList}
     */
    abstract protected StorageResourceList storageResourceList(String namespace, @Nullable String pv, @Nullable String pvc, @Nullable Integer sizeGb);

    /**
     * Generate secret Yaml resource.
     *
     * @param namespace        In which namespace the secret will be created
     * @param name             The name secret will be created
     * @param registry         The registry address e.g. index.docker.io
     * @param registryUsername Registry username e.g. your docker hub username
     * @param registryPassword Registry password e.g. your docker hub password
     * @return {@link  SecretResource}
     */
    abstract protected SecretResource dockersecret(String namespace, @Nullable String name, String registry, String registryUsername, String registryPassword);
}
