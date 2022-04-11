package io.hotcloud.buildpack.api.clone;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.security.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class GitClonedServiceIT extends BuildPackIntegrationTestBase {

    @Autowired
    private UserApi userApi;
    @Autowired
    private GitClonedService gitClonedService;

    @Before
    public void before() {

        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Test
    public void cloned() throws InterruptedException {
        String gitUrl = "https://gitee.com/yannanshan/devops-thymeleaf.git";
        gitClonedService.clone(gitUrl, null, null, null, null);

        gitClonedService.deleteOne("admin", "devops-thymeleaf");
        GitCloned cloned = null;
        while (null == cloned) {
            TimeUnit.SECONDS.sleep(5);
            cloned = gitClonedService.findOne("admin", "devops-thymeleaf");
        }

        Assertions.assertEquals(gitUrl, cloned.getUrl());
        Assertions.assertEquals("Dockerfile", cloned.getDockerfile());
        Assertions.assertFalse(StringUtils.hasText(cloned.getError()));
    }

}
