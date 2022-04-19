package io.hotcloud.security.server.user;

import io.hotcloud.security.SecurityApplicationRunnerPostProcessor;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.api.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class SecurityApplicationInternalUserPostProcessor implements SecurityApplicationRunnerPostProcessor {

    private final UserApi userApi;
    private final PasswordEncoder passwordEncoder;

    public SecurityApplicationInternalUserPostProcessor(UserApi userApi,
                                                        PasswordEncoder passwordEncoder) {
        this.userApi = userApi;
        this.passwordEncoder = passwordEncoder;
    }

    private void internalUserSaved(String username) {
        if (!userApi.exist(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea"))
                    .nickname(username)
                    .build();
            User saved = userApi.save(user);
            log.info("SecurityApplicationInternalUserPostProcessor. {} user created", saved.getUsername());
        }
    }

    @Override
    public void execute() {
        Stream.of("admin", "guest", "clientuser").forEach(this::internalUserSaved);
    }
}
