package io.hotcloud.buildpack.server.buildpack.processor;

import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.User;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static io.hotcloud.security.api.UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
class BuildPackApplicationUserNamespacePostProcessor implements BuildPackApplicationRunnerPostProcessor {

    private final UserApi userApi;
    private final Cache cache;
    private final NamespaceApi namespaceApi;

    public BuildPackApplicationUserNamespacePostProcessor(UserApi userApi,
                                                          Cache cache,
                                                          NamespaceApi namespaceApi) {
        this.userApi = userApi;
        this.cache = cache;
        this.namespaceApi = namespaceApi;
    }

    @Override
    public void execute() {
        Collection<User> users = userApi.users();
        Assert.state(!CollectionUtils.isEmpty(users), "Users is empty", 400);

        users.stream()
                .map(User::getUsername)
                .forEach(this::cachedUserNamespace);

        boolean anyMatch = users
                .stream()
                .map(User::getUsername)
                .anyMatch("guest"::equalsIgnoreCase);
        if (!anyMatch) {
            this.cachedUserNamespace("guest");
            log.info("BuildPackApplicationUserNamespacePostProcessor. {} user namespace cached", users.size() + 1);
            return;
        }
        log.info("BuildPackApplicationUserNamespacePostProcessor. {} user namespace cached", users.size());
    }

    private void cachedUserNamespace(String username) {
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, username), NamespaceGenerator::uuidNoDashNamespace);
        if (namespaceApi.read(namespace) == null) {
            try {
                namespaceApi.namespace(namespace);
            } catch (ApiException e) {
                throw new HotCloudException(e.getMessage(), e);
            }
        }
        cache.put(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, username), namespace);
    }
}
