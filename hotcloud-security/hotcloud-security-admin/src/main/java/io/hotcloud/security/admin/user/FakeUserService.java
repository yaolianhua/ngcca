package io.hotcloud.security.admin.user;

import com.github.javafaker.Faker;
import io.hotcloud.security.api.FakeUserApi;
import io.hotcloud.security.user.FakeUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Service
public class FakeUserService implements FakeUserApi {

    private static final Faker FAKER = new Faker();
    private static final String PASSWORD = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(UUID.randomUUID().toString());
    private final static Collection<UserDetails> FAKE_USER_LIST = new ArrayList<>();

    static {
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
        return FAKE_USER_LIST.stream()
                .filter(e -> Objects.equals(username, e.getUsername()))
                .map(e -> ((FakeUser) e))
                .findFirst().orElse(null);
    }

    @Override
    public Collection<UserDetails> users() {
        return FAKE_USER_LIST.stream().map(e -> ((FakeUser) e)).collect(Collectors.toList());
    }
}
