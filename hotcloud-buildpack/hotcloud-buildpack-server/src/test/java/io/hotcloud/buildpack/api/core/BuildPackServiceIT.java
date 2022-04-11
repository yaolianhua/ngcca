package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.security.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
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
public class BuildPackServiceIT extends BuildPackIntegrationTestBase {

    @Autowired
    private UserApi userApi;

    @Autowired
    private AbstractBuildPackPlayer abstractBuildPackPlayer;

    @Autowired
    private GitClonedService gitClonedService;

    @Autowired
    private BuildPackService buildPackService;

    @Before
    public void before() {

        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @After
    public void after() {
        buildPackService.deleteAll();
    }

    public GitCloned cloned(String url) throws InterruptedException {
        gitClonedService.clone(url, null, null, null, null);

        String project = GitCloned.retrieveGitProject(url);
        gitClonedService.deleteOne("admin", project);
        GitCloned cloned = null;
        while (null == cloned) {
            TimeUnit.SECONDS.sleep(5);
            cloned = gitClonedService.findOne("admin", "devops-thymeleaf");
        }

        Assertions.assertEquals(url, cloned.getUrl());
        Assertions.assertFalse(StringUtils.hasText(cloned.getError()));

        return cloned;
    }

    @Test
    public void saved_then_findAll() throws InterruptedException {

        String gitUrl = "https://gitee.com/yannanshan/devops-thymeleaf.git";
        GitCloned cloned = cloned(gitUrl);

        BuildPack buildpack = abstractBuildPackPlayer.buildpack(gitUrl,
                "Dockerfile",
                true);

        buildPackService.save(cloned.getId(), buildpack);

        BuildPack find = buildPackService.findOneWithNoDone("admin", cloned.getId());
        Assertions.assertNotNull(find);

        Assertions.assertEquals(buildpack.getBuildPackYaml(), find.getBuildPackYaml());
        Assertions.assertEquals(buildpack.getJob(), find.getJob());
    }
}
