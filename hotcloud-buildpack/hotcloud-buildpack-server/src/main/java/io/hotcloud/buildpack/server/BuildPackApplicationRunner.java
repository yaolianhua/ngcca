package io.hotcloud.buildpack.server;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.security.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackApplicationRunner implements ApplicationRunner {

    private final ApplicationContext applicationContext;

    public BuildPackApplicationRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Cache cache = applicationContext.getBean(Cache.class);
        UserApi userApi = applicationContext.getBean(UserApi.class);


    }
}
