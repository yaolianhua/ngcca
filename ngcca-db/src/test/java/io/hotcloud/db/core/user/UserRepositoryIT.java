package io.hotcloud.db.core.user;

import com.github.javafaker.Faker;
import io.hotcloud.db.NgccaDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class UserRepositoryIT extends NgccaDBApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @After
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    public void readByUsername() {
        UserEntity userEntity = buildUser("Jason");
        userRepository.save(userEntity);
        Assertions.assertNotNull(userEntity.getId());

        UserEntity entity = userRepository.findByUsername("Jason");
        Assertions.assertNotNull(entity);
        Assertions.assertTrue(entity.isEnabled());
        Assertions.assertEquals("{bcrypt}$2a$10$gMTHRvoXUqVvTP2FTyscKump04YidOlhA/3rR6X6nuhlemNnlt5xS", entity.getPassword());

        userRepository.delete(userEntity);
    }

    @Test
    public void readAll() {

        Faker faker = new Faker();
        List<UserEntity> users = IntStream.range(0, 10)
                .mapToObj(i -> faker.name().username())
                .map(this::buildUser)
                .collect(Collectors.toList());
        userRepository.saveAll(users);

        PageRequest pageRequest = PageRequest.of(1, 5);
        Page<UserEntity> page = userRepository.findAll(pageRequest);

        Assertions.assertEquals(10, page.getTotalElements());
        Assertions.assertEquals(5, page.getContent().size());
        Assertions.assertEquals(2, page.getTotalPages());

        userRepository.deleteAll(users);
    }

    private UserEntity buildUser(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword("{bcrypt}$2a$10$gMTHRvoXUqVvTP2FTyscKump04YidOlhA/3rR6X6nuhlemNnlt5xS");
        user.setNickname(username);
        user.setEmail(String.format("%s@gmail.com", username));
        user.setEnabled(true);
        user.setAvatar("https://avatars.githubusercontent.com/u/29155254?v=4");

        return user;
    }
}
