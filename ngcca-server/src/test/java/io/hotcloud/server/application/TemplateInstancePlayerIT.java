package io.hotcloud.server.application;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.module.application.template.Template;
import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstancePlayer;
import io.hotcloud.module.application.template.TemplateInstanceService;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.server.CoreServerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CoreServerApplication.class
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
    public void play() {

        AtomicInteger errors = new AtomicInteger(0);
        AtomicBoolean done = new AtomicBoolean(false);

        for (Template template : Template.values()) {
            try {
                player.play(template);
            } catch (Exception e) {
                Log.warn(this, null, Event.EXCEPTION, e.getMessage());
                errors.incrementAndGet();
            }
        }

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            long success = templateService.findAll("admin")
                    .stream()
                    .filter(TemplateInstance::isSuccess)
                    .count();
            boolean succeed = Template.values().length - errors.get() == success;
            done.set(true);
            return succeed;
        });

        Assertions.assertTrue(done.get());

    }

    @Test
    public void delete() {

        for (TemplateInstance templateInstance : templateService.findAll("admin")) {
            player.delete(templateInstance.getId());
        }

        await().atMost(10, TimeUnit.SECONDS).until(() -> templateService.findAll("admin").isEmpty());
    }
}
