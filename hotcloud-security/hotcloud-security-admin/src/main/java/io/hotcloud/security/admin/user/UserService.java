package io.hotcloud.security.admin.user;

import io.hotcloud.common.Assert;
import io.hotcloud.db.api.user.UserEntity;
import io.hotcloud.db.api.user.UserRepository;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean exist(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public UserDetails save(User user) {
        Assert.notNull(user, "User body is null", 400);
        Assert.hasText(user.getUsername(), "username is null", 400);
        Assert.hasText(user.getPassword(), "password is null", 400);

        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(user, entity);

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
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
        //TODO
        return false;
    }

    @Override
    public void deleteAll(boolean physically) {
        if (physically) {
            userRepository.deleteAll();
        }
        //TODO
    }

    @Override
    public UserDetails retrieve(String username) {
        UserEntity entity = userRepository.findByUsername(username);
        Assert.notNull(entity, "Retrieve user null [" + username + "]", 404);

        return buildUser(entity);
    }

    @Override
    public UserDetails current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "Authentication is null", 401);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Assert.notNull(userDetails, "UserDetails is null", 401);

        return retrieve(userDetails.getUsername());
    }

    @Override
    public Collection<UserDetails> users() {
        Collection<UserDetails> users = new ArrayList<>();
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
