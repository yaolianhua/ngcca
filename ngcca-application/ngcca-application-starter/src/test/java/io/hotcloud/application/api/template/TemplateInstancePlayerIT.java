package io.hotcloud.application.api.template;

import io.hotcloud.application.NgccaApplicationTest;
import io.hotcloud.security.api.user.UserApi;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TemplateInstancePlayerIT extends NgccaApplicationTest {

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
