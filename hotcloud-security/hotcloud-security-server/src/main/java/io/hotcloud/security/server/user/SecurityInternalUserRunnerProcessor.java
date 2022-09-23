package io.hotcloud.security.server.user;

import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.common.api.CommonRunnerProcessor;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.UUIDGenerator;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class SecurityInternalUserRunnerProcessor implements CommonRunnerProcessor {

    private final UserApi userApi;
    private final Cache cache;

    public SecurityInternalUserRunnerProcessor(UserApi userApi, Cache cache) {
        this.userApi = userApi;
        this.cache = cache;
    }

    private void internalUserSaved(String username) {
        if (!userApi.exist(username)) {
            User user = User.builder()
                    .username(username)
                    .password("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea")
                    .nickname(username)
                    .enabled(true)
                    .build();
            User saved = userApi.save(user);
            Log.info(SecurityInternalUserRunnerProcessor.class.getName(), String.format("%s user created", saved.getUsername()));
        }

        String namespace = UUIDGenerator.uuidNoDash();
        Object o = cache.putIfAbsent(String.format(CommonConstant.CK_NAMESPACE_USER_KEY_PREFIX, username), namespace);
        if (Objects.isNull(o)){
            Log.info(SecurityInternalUserRunnerProcessor.class.getName(),
                    String.format("user '%s' namespace '%s' cached", username, namespace));
        }

    }

    @Override
    public void execute() {
        Stream.of("admin", "guest", "clientuser").forEach(this::internalUserSaved);
    }
}
