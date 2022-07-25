package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.buildpack.api.core.event.BuildPackStartedEventV2;
import io.hotcloud.common.api.Validator;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import io.hotcloud.security.api.user.UserNamespacePair;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static io.hotcloud.security.api.SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX;

@Component
@RequiredArgsConstructor
public class DefaultBuildPackPlayerV2 implements BuildPackPlayerV2 {

    private final BuildPackApiV2 buildPackApiV2;
    private final UserApi userApi;
    private final Cache cache;
    private final NamespaceApi namespaceApi;
    private final BuildPackService buildPackService;
    private final ApplicationEventPublisher eventPublisher;

    @SneakyThrows
    @Override
    public BuildPack play(String httpGitUrl, String branch) {

        Assert.state(Validator.validHTTPGitAddress(httpGitUrl), "Http git url invalid");
        Assert.state(StringUtils.hasText(branch), "Git branch is null");

        UserNamespacePair userNamespacePair = retrievedUserNamespacePair();
        List<BuildPack> buildPacks = buildPackService.findAll(userNamespacePair.getUsername());
        boolean buildTaskExisted = buildPacks.stream()
                .filter(e -> Objects.equals(httpGitUrl, e.getHttpGitUrl()))
                .filter(e -> Objects.equals(branch, e.getGitBranch()))
                .anyMatch(e -> Objects.equals(false, e.isDone()));
        Assert.state(!buildTaskExisted, String.format("ImageBuild task is running. user:%s gitUrl:%s branch:%s",
                userNamespacePair.getUsername(), httpGitUrl, branch));

        if (Objects.isNull(namespaceApi.read(userNamespacePair.getNamespace()))){
            namespaceApi.create(userNamespacePair.getNamespace());
        }

        BuildPack buildPack = buildPackApiV2.apply(userNamespacePair.getNamespace(), httpGitUrl, branch);

        buildPack.setUser(userNamespacePair.getUsername());
        buildPack.setArtifact(buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
        buildPack.setHttpGitUrl(httpGitUrl);
        buildPack.setGitBranch(branch);
        buildPack.setDeleted(false);
        buildPack.setDone(false);

        BuildPack saved = buildPackService.saveOrUpdate(buildPack);

        eventPublisher.publishEvent(new BuildPackStartedEventV2(saved));

        return saved;
    }

    @Override
    public void delete(String id, boolean physically) {

    }

    @NotNull
    private UserNamespacePair retrievedUserNamespacePair() {
        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null");

        //get user's namespace.
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");
        return new UserNamespacePair(current.getUsername(), namespace);
    }
}
