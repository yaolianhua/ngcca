package io.hotcloud.server.security;

import io.hotcloud.common.model.utils.Log;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SecuritySystemUserInitialization implements ApplicationRunner {

    private final UserApi userApi;

    public SecuritySystemUserInitialization(UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stream.of("admin", "guest").forEach(this::internalUserSaved);
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
            Log.info(SecuritySystemUserInitialization.class.getName(), String.format("%s user created", saved.getUsername()));
        }
    }
}
