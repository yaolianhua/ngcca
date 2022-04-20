package io.hotcloud.security.server.user;

import io.hotcloud.common.Validator;
import io.hotcloud.db.core.user.UserEntity;
import io.hotcloud.db.core.user.UserRepository;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import io.hotcloud.security.api.user.event.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean exist(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public User save(User user) {
        Assert.notNull(user, "User body is null");
        Assert.hasText(user.getUsername(), "username is null");
        Assert.hasText(user.getPassword(), "password is null");

        Assert.state(Validator.validUsername(user.getUsername()), "Start with a lowercase letter, can only contain lowercase letters and numbers, [5-16] characters");
        UserEntity entity = (UserEntity) new UserEntity().copyToEntity(user);

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        UserEntity saved = userRepository.save(entity);

        user = saved.toT(User.class);
        eventPublisher.publishEvent(new UserCreatedEvent(user));
        return buildUser(saved);
    }

    @Override
    public User update(User user) {
        Assert.notNull(user, "User body is null");
        Assert.hasText(user.getId(), "user id is null");

        UserEntity existEntity = userRepository.findById(user.getId()).orElseThrow(() -> new NullPointerException("User is not found"));
        if (StringUtils.hasText(user.getAvatar())) {
            existEntity.setAvatar(user.getAvatar());
        }
        if (StringUtils.hasText(user.getMobile())) {
            existEntity.setMobile(user.getMobile());
        }
        if (StringUtils.hasText(user.getEmail())) {
            existEntity.setEmail(user.getEmail());
        }
        if (StringUtils.hasText(user.getNickname())) {
            existEntity.setNickname(user.getNickname());
        }
        if (StringUtils.hasText(user.getPassword())) {
            existEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        UserEntity updated = userRepository.save(existEntity);

        return buildUser(updated);
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
        Assert.notNull(entity, "Retrieve user null [" + username + "]");

        return buildUser(entity);
    }

    @Override
    public User current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "Authentication is null");
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Assert.notNull(userDetails, "UserDetails is null");

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
                .id(entity.getId())
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
