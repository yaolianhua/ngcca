package io.hotcloud.security.server.user;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.security.HotCloudSecurityApplicationTest;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
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

import static io.hotcloud.security.api.user.UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = HotCloudSecurityApplicationTest.class
)
@Slf4j
@ActiveProfiles("security-integration-test")
public class UserApiIT {

    @Autowired
    private UserApi userApi;
    @Autowired
    private Cache cache;

    @Before
    public void authenticated() {
        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @After
    public void clear() {
        cache.clear();
        userApi.deleteAll(true);
    }

    @Test
    public void save() throws InterruptedException {
        User user = User.builder()
                .username("jason")
                .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("jason123"))
                .nickname("Jason")
                .build();
        userApi.save(user);

        TimeUnit.SECONDS.sleep(1);
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, user.getUsername()), String.class);
        Assertions.assertNotNull(namespace);
    }

    @Test
    public void users() {

        Collection<User> users = userApi.users();
        //SecurityApplicationRunner
        Assertions.assertFalse(CollectionUtils.isEmpty(users));

        //In order to test the data is in different database
        cache.put(UserApi.CACHE_USERS_KEY_PREFIX, users);

        for (User user : users) {
            log.info("{}", user);
            Assertions.assertNotNull(userApi.retrieve(user.getUsername()));
        }

        User current = userApi.current();
        Assertions.assertNotNull(current);
        Assertions.assertEquals("admin", current.getUsername());
    }
}
