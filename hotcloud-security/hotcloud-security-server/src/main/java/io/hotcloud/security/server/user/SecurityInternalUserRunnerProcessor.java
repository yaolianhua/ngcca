package io.hotcloud.security.server.user;

import io.hotcloud.common.api.Log;
import io.hotcloud.security.SecurityRunnerProcessor;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class SecurityInternalUserRunnerProcessor implements SecurityRunnerProcessor {

    private final UserApi userApi;

    public SecurityInternalUserRunnerProcessor(UserApi userApi) {
        this.userApi = userApi;
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
    }

    @Override
    public void execute() {
        Stream.of("admin", "guest", "clientuser").forEach(this::internalUserSaved);
    }
}
