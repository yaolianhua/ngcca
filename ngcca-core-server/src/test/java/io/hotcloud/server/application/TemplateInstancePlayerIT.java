package io.hotcloud.server.application;

import io.hotcloud.application.NgccaApplication;
import io.hotcloud.security.api.user.UserApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgccaApplication.class
)
@ActiveProfiles("test")
public class TemplateInstancePlayerIT  {

    @Autowired
    private TemplateInstancePlayer player;
    @Autowired
    private UserApi userApi;
    @Autowired
    private TemplateInstanceService templateService;

    @Before
    public void before() {
        UserDetails adminUserDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken adminUsernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    @Test
    public void play() throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(1);

        for (Template template : Template.values()) {
            player.play(template);
        }

        while (downLatch.getCount() != 0) {
            TimeUnit.SECONDS.sleep(10);
            List<TemplateInstance> admins = templateService.findAll("admin");
            long success = admins.stream()
                    .filter(TemplateInstance::isSuccess)
                    .count();
            if (Template.values().length == success) {
                downLatch.countDown();
            }
        }

        System.out.println("All template deploy success!");

    }

    @Test
    public void delete() throws InterruptedException {
        List<TemplateInstance> admins = templateService.findAll("admin");
        for (TemplateInstance templateInstance : admins) {
            player.delete(templateInstance.getId());
        }

        TimeUnit.SECONDS.sleep(10);
    }
}
