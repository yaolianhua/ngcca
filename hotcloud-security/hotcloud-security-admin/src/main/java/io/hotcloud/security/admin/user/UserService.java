package io.hotcloud.security.admin.user;

import io.hotcloud.common.Assert;
import io.hotcloud.db.api.user.UserEntity;
import io.hotcloud.db.api.user.UserRepository;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class UserService implements UserApi {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean exist(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public User save(User user) {
        Assert.notNull(user, "User body is null", 400);
        Assert.hasText(user.getUsername(), "username is null", 400);
        Assert.hasText(user.getPassword(), "password is null", 400);

        UserEntity entity = (UserEntity) new UserEntity().copyToEntity(user);

        UserEntity saved = userRepository.save(entity);

        return buildUser(saved);
    }

    @Override
    public boolean delete(String username, boolean physically) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        if (physically) {
            return userRepository.deleteByUsername(username);
        }

        UserEntity entity = userRepository.findByUsername(username);
        entity.setEnabled(false);

        userRepository.save(entity);
        return true;
    }

    @Override
    public void deleteAll(boolean physically) {
        if (physically) {
            userRepository.deleteAll();
        }
        Iterable<UserEntity> entities = userRepository.findAll();
        entities.forEach(e -> e.setEnabled(false));
        userRepository.saveAll(entities);
    }

    @Override
    public User retrieve(String username) {
        UserEntity entity = userRepository.findByUsername(username);
        Assert.notNull(entity, "Retrieve user null [" + username + "]", 404);

        return buildUser(entity);
    }

    @Override
    public User current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "Authentication is null", 401);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Assert.notNull(userDetails, "UserDetails is null", 401);

        return retrieve(userDetails.getUsername());
    }

    @Override
    public Collection<User> users() {
        Collection<User> users = new ArrayList<>();
        for (UserEntity entity : userRepository.findAll()) {
            users.add(buildUser(entity));
        }
        return users;
    }

    private User buildUser(UserEntity entity) {
        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .avatar(entity.getAvatar())
                .enabled(entity.isEnabled())
                .mobile(entity.getMobile())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .build();
    }
}
