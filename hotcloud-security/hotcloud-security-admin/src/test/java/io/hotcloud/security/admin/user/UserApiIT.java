package io.hotcloud.security.admin.user;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.security.HotCloudSecurityApplicationTest;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.FakeUser;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        UserDetails userDetails = userApi.retrieve("client-user");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @After
    public void clear() {
        cache.clear();
    }

    @Test
    public void userApi() {

        Collection<UserDetails> users = userApi.users();
        Assertions.assertFalse(CollectionUtils.isEmpty(users));

        List<FakeUser> fakeUsers = users.stream().map(e -> ((FakeUser) e))
                .collect(Collectors.toList());

        for (FakeUser fakeUser : fakeUsers) {
            log.info("{}", fakeUser);
            Assertions.assertNotNull(userApi.retrieve(fakeUser.getUsername()));
        }

        FakeUser current = (FakeUser) userApi.current();
        Assertions.assertNotNull(current);
        Assertions.assertEquals("client-user", current.getUsername());
    }
}
