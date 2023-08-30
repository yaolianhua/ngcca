package io.hotcloud.service.runner;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializeProcessor implements RunnerProcessor {

    private final UserApi userApi;

    public AdminUserInitializeProcessor(UserApi userApi) {
        this.userApi = userApi;
    }

    @Override
    public void execute() {
        boolean exist = userApi.exist(CommonConstant.ADMIN_USERNAME);
        if (exist) {
            return;
        }

        User user = User.builder()
                .username(CommonConstant.ADMIN_USERNAME)
                .password(CommonConstant.ADMIN_INIT_PASSWORD)
                .nickname(CommonConstant.ADMIN_USERNAME)
                .enabled(true)
                .build();
        User saved = userApi.save(user);
        Log.info(this, saved, Event.START, "system user init success");

    }

}
