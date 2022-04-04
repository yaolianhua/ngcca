package io.hotcloud.buildpack.server.buildpack.processor;

import io.hotcloud.buildpack.BuildPackApplicationRunnerPostProcessor;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.hotcloud.security.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    public BuildPackApplicationUserNamespacePostProcessor(UserApi userApi, Cache cache) {
        this.userApi = userApi;
        this.cache = cache;
    }

    @Override
    public void execute() {
        Collection<UserDetails> users = userApi.users();
        users.forEach(user -> cache.putIfAbsent(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()),
                NamespaceGenerator.uuidNoDashNamespace()));

        boolean anyMatch = users.stream().map(UserDetails::getUsername)
                .anyMatch("admin"::equalsIgnoreCase);
        if (!anyMatch) {
            cache.putIfAbsent(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, "admin"), NamespaceGenerator.uuidNoDashNamespace());
            log.info("BuildPackApplicationUserNamespacePostProcessor. {} user namespace cached", users.size() + 1);
            return;
        }
        log.info("BuildPackApplicationUserNamespacePostProcessor. {} user namespace cached", users.size());
    }
}
