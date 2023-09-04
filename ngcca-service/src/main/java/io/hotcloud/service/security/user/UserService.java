package io.hotcloud.service.security.user;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.ResourceNotFoundException;
import io.hotcloud.common.utils.Validator;
import io.hotcloud.db.entity.UserEntity;
import io.hotcloud.db.entity.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
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
    public User save(User user) {
        Assert.notNull(user, "User body is null");
        Assert.hasText(user.getUsername(), "username is null");
        Assert.hasText(user.getPassword(), "password is null");

        Assert.state(Validator.validUsername(user.getUsername()), "username must be start with a lowercase letter and contain lowercase letters and numbers only! [5-16] characters");
        Assert.isTrue(!exist(user.getUsername()), "username [" + user.getUsername() + "] already exist!");

        UserEntity entity = (UserEntity) new UserEntity().toE(user);

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setCreatedAt(LocalDateTime.now());
        UserEntity saved = userRepository.save(entity);

        return buildUser(saved);
    }

    @Override
    public User update(User user) {
        Assert.notNull(user, "User body is null");
        Assert.hasText(user.getId(), "user id is null");

        UserEntity existEntity = userRepository.findById(user.getId()).orElseThrow(() -> new ResourceNotFoundException("User is not found"));
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

        existEntity.setModifiedAt(LocalDateTime.now());
        UserEntity updated = userRepository.save(existEntity);

        return buildUser(updated);
    }

    @Override
    public void switchUser(String username, Boolean onOff) {
        Assert.hasText(username, "username is null");
        Assert.notNull(onOff, "user switch is null");
        boolean exist = this.exist(username);
        Assert.isTrue(exist, "user [" + username + "] can not be found");

        UserEntity entity = userRepository.findByUsername(username);
        entity.setEnabled(onOff);

        userRepository.save(entity);
    }

    @Override
    public void delete(String username, boolean physically) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        if (physically) {
            userRepository.deleteByUsername(username);
            return;
        }

        UserEntity entity = userRepository.findByUsername(username);
        Assert.notNull(entity, "User not found [" + username + "]");
        entity.setEnabled(false);

        userRepository.save(entity);
    }

    @Override
    public void deleteByUserid(String id, boolean physically) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        if (physically) {
            userRepository.deleteById(id);
            return;
        }

        UserEntity entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found [" + id + "]"));
        entity.setEnabled(false);

        userRepository.save(entity);
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
        if (entity == null) {
            Log.warn(this, username, "retrieve user null");
            return null;
        }

        return buildUser(entity);
    }

    @Override
    public User find(String id) {
        UserEntity entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found [" + id + "]"));
        return buildUser(entity);
    }

    @Override
    public User current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assert.notNull(authentication, "Authentication is null");
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return retrieve(userDetails.getUsername());
        }

        return null;
    }

    @Override
    public Collection<User> users() {
        Collection<User> users = new ArrayList<>();
        for (UserEntity entity : userRepository.findAll()) {
            users.add(buildUser(entity));
        }
        return users;
    }

    @Override
    public Collection<User> usersLike(String username) {
        return userRepository.findByUsernameLike(username)
                .stream()
                .map(this::buildUser)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAdmin(String username) {
        User user = this.retrieve(username);
        return Objects.equals("admin", user.getUsername());
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
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
