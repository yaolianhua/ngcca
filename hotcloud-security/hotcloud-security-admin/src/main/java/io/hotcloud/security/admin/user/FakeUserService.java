package io.hotcloud.security.admin.user;

import com.github.javafaker.Faker;
import io.hotcloud.common.Assert;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.security.api.FakeUserApi;
import io.hotcloud.security.user.FakeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Service
public class FakeUserService implements FakeUserApi {
    private static final String PASSWORD = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea");
    private final Cache cache;

    private static final Faker FAKER = new Faker();

    public FakeUserService(Cache cache) {
        this.cache = cache;

        List<FakeUser> fakeUsers = FAKE_USER_LIST.stream().map(e -> ((FakeUser) e)).collect(Collectors.toList());
        cache.putIfAbsent(CACHE_USERS_KEY_PREFIX, fakeUsers);

        Collection<UserDetails> users = cache.get(CACHE_USERS_KEY_PREFIX, () -> FAKE_USER_LIST);
        users.forEach(e -> cache.putIfAbsent(String.format(CACHE_USER_KEY_PREFIX, e.getUsername()), e));
    }

    private final static Collection<UserDetails> FAKE_USER_LIST = new ArrayList<>();


    static {
        FAKE_USER_LIST.add(FakeUser.of("client-user", "client-user",
                PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("e2c20178-1f6b-4860-b9d2-7ac4a9f2a2ea")));
        for (int i = 0; i < 10; i++) {
            String username = FAKER.name().username();
            FakeUser fakeUser = FakeUser.of(username, FAKER.name().fullName(), PASSWORD);
            List<String> usernames = FAKE_USER_LIST.stream()
                    .map(UserDetails::getUsername)
                    .collect(Collectors.toList());

            if (usernames.contains(username)) {
                continue;
            }
            FAKE_USER_LIST.add(fakeUser);
        }

    }

    @Override
    public FakeUser retrieve(String username) {
        FakeUser fakeUser = cache.get(String.format(CACHE_USER_KEY_PREFIX, username), FakeUser.class);
        Assert.notNull(fakeUser, "Retrieve user null [" + username + "]", 404);
        return fakeUser;
    }

    @Override
    public FakeUser current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "Authentication is null", 401);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Assert.notNull(userDetails, "UserDetails is null", 401);

        return retrieve(userDetails.getUsername());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<UserDetails> users() {
        return cache.get(CACHE_USERS_KEY_PREFIX, List.class);
    }
}
