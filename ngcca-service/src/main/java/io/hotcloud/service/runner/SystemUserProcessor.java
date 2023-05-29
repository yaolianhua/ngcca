package io.hotcloud.service.runner;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import org.springframework.stereotype.Component;

@Component
public class SystemUserProcessor implements RunnerProcessor {

    private final UserApi userApi;

    public SystemUserProcessor(UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public void execute() {
        String username = "admin";
        boolean exist = userApi.exist(username);
        if (exist) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea")
                .nickname(username)
                .enabled(true)
                .build();
        User saved = userApi.save(user);
        Log.info(this, saved, Event.START, "system user init success");

    }

}
