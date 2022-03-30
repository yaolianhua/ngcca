package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.api.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.BuildPackApiAdaptor;
import io.hotcloud.buildpack.api.KanikoFlag;
import io.hotcloud.buildpack.api.model.BuildPack;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.common.StringHelper;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.hotcloud.security.api.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Component
public class BuildPackApi implements BuildPackApiAdaptor {

    private final AbstractBuildPackApi abstractBuildPackApi;
    private final UserApi userApi;
    private final KanikoFlag kanikoFlag;
    private final BuildPackStorageProperties storageProperties;
    private final Cache cache;
    private final NamespaceApi namespaceApi;

    public BuildPackApi(AbstractBuildPackApi abstractBuildPackApi,
                        UserApi userApi,
                        KanikoFlag kanikoFlag,
                        BuildPackStorageProperties storageProperties,
                        Cache cache,
                        NamespaceApi namespaceApi) {
        this.abstractBuildPackApi = abstractBuildPackApi;
        this.userApi = userApi;
        this.kanikoFlag = kanikoFlag;
        this.storageProperties = storageProperties;
        this.cache = cache;
        this.namespaceApi = namespaceApi;
    }

    @Override
    public BuildPack buildpack(String gitUrl, String dockerfile, boolean force, String registry, String registryProject, String registryUser, String registryPass) {

        UserDetails current = userApi.current();
        Assert.notNull(current, "Retrieve current user null", 404);
        Assert.hasText(current.getUsername(), "Current user's username is null", 404);

        //get user's namespace. all user's namespace will be cached when application started
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), NamespaceGenerator::uuidNoDashNamespace);
        //create user's namespace
        try {
            namespaceApi.namespace(namespace);
        } catch (ApiException e) {
            throw new HotCloudException(String.format("Namespace '%s' create failed [%s]", namespace, e.getMessage()));
        }

        String gitProject = StringHelper.retrieveProjectFromHTTPGitUrl(gitUrl);
        //handle kaniko args
        registry = StringUtils.hasText(registry) ? registry : kanikoFlag.getInsecureRegistry();
        Map<String, String> args = resolvedArgs(gitUrl, dockerfile, registry, registryProject);

        //repository clone path locally, it will be mounted by user pod
        String clonePath = null;
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            clonePath = Path.of(storageProperties.getHostPath().getPath(), namespace, gitProject).toString();
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            clonePath = Path.of(storageProperties.getNfs().getPath(), namespace, gitProject).toString();
        }
        Assert.hasText(clonePath, "repository clone path is null", 404);

        //registry may be fully public
        registryUser = StringUtils.hasText(registryUser) ? registryUser : "no-auth-user";
        registryPass = StringUtils.hasText(registryPass) ? registryPass : "no-auth-pass";

        return abstractBuildPackApi.buildpack(namespace, gitUrl, clonePath, force, registry, registryUser, registryPass, args);
    }

    private Map<String, String> resolvedArgs(String gitUrl, String dockerfile, String registry, String registryProject) {
        Map<String, String> args = kanikoFlag.resolvedArgs();
        String pushedImage = StringHelper.generatePushedImage(gitUrl);
        String tarball = StringHelper.generateImageTarball(gitUrl);

        if (StringUtils.hasText(dockerfile)) {
            args.put("dockerfile", String.format("/workspace/%s", dockerfile));
        }
        if (StringUtils.hasText(registry)) {
            args.put("insecure-registry", registry);
        }
        args.put("tarPath", String.format("/workspace/%s", tarball));

        if (!StringUtils.hasText(kanikoFlag.getDestination())) {
            registryProject = StringUtils.hasText(registryProject) ? registryProject : "registry-project-name";
            String registryGet = args.getOrDefault("insecure-registry", "index.docker.io");

            //index.docker.io/example/image-name:latest
            String destination = String.format("%s/%s/%s", registryGet, registryProject, pushedImage);
            args.put("destination", destination);
        } else {
            args.put("destination", kanikoFlag.getDestination() + pushedImage);
        }

        return args;
    }
}
