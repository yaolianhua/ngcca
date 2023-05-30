package io.hotcloud.service.db;

import com.github.javafaker.Faker;
import io.hotcloud.module.db.entity.UserEntity;
import io.hotcloud.module.db.entity.UserRepository;
import io.hotcloud.server.NgccaCoreServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaCoreServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
@ActiveProfiles("test")
public class UserRepositoryIT {

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

        userRepository.deleteAll();
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
