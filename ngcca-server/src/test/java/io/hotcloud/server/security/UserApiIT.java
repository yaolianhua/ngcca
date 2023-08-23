package io.hotcloud.server.security;

import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.server.NgccaServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = NgccaServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Slf4j
public class UserApiIT {

    @Autowired
    private UserApi userApi;

    @Before
    public void authenticated() {
        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @After
    public void clear() {
        userApi.deleteAll(true);
    }

    @Test
    public void save_then_update() throws InterruptedException {
        User user = User.builder()
                .username("jason")
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("jason123"))
                .nickname("Jason")
                .build();
        User saved = userApi.save(user);
        Assertions.assertNotNull(saved.getId());

        TimeUnit.SECONDS.sleep(1);

        Assertions.assertNotNull(saved.getNamespace());

        saved.setEmail("example.com");
        userApi.update(saved);
        User query = userApi.retrieve("jason");
        Assertions.assertEquals("example.com", query.getEmail());

    }

    @Test
    public void users() {

        Collection<User> users = userApi.users();
        //SecuritySystemUserInitialization
        Assertions.assertFalse(CollectionUtils.isEmpty(users));

        //In order to test the data is in different database

        for (User user : users) {
            log.info("{}", user);
            Assertions.assertNotNull(userApi.retrieve(user.getUsername()));
        }

        User current = userApi.current();
        Assertions.assertNotNull(current);
        Assertions.assertEquals("admin", current.getUsername());
    }
}
