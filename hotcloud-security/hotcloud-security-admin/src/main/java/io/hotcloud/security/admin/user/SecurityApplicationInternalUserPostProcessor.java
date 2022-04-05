package io.hotcloud.security.admin.user;

import io.hotcloud.security.SecurityApplicationRunnerPostProcessor;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    @Override
    public void execute() {
        boolean adminExisted = userApi.exist("admin");
        boolean clientUserExisted = userApi.exist("client-user");

        if (!adminExisted) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea"))
                    .nickname("administrator")
                    .enabled(true)
                    .build();
            UserDetails saved = userApi.save(adminUser);
            log.info("SecurityApplicationInternalUserPostProcessor. {} user created", saved.getUsername());
        }
        if (!clientUserExisted) {
            User clientUser = User.builder()
                    .username("client-user")
                    .password(passwordEncoder.encode("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea"))
                    .nickname("client-user")
                    .enabled(true)
                    .build();
            UserDetails saved = userApi.save(clientUser);
            log.info("SecurityApplicationInternalUserPostProcessor. {} user created", saved.getUsername());
        }

    }
}
