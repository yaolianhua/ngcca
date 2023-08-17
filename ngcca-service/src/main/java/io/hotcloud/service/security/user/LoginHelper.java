package io.hotcloud.service.security.user;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.module.security.jwt.JwtSigner;
import io.hotcloud.module.security.jwt.JwtVerifier;
import io.hotcloud.module.security.login.BearerToken;
import io.hotcloud.module.security.login.LoginApi;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

@Service
public class LoginHelper implements LoginApi {

    private final UserApi userApi;
    private final JwtSigner jwtSigner;
    private final JwtVerifier jwtVerifier;

    private final PasswordEncoder passwordEncoder;

    public LoginHelper(UserApi userApi,
                       JwtSigner jwtSigner,
                       JwtVerifier jwtVerifier,
                       PasswordEncoder passwordEncoder) {
        this.userApi = userApi;
        this.jwtSigner = jwtSigner;
        this.jwtVerifier = jwtVerifier;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BearerToken basicLogin(String username, String password) {
        Assert.hasText(username, "username is null");
        Assert.hasText(password, "password is null");
        try {
            User retrieved = userApi.retrieve(username);
            boolean matches = passwordEncoder.matches(password, retrieved.getPassword());
            Assert.isTrue(matches, "Invalid password");

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(retrieved, null, retrieved.getAuthorities());
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            String token = jwtSigner.sign(Map.of("username", retrieved.getUsername()));
            return BearerToken.of(token);
        } catch (Exception ex) {
            throw new PlatformException("Invalid username or password");
        }

    }

    @Override
    public User retrieveUser(String authorization) {
        if (authorization.startsWith("Bearer") || authorization.startsWith("bearer")) {
            authorization = authorization.substring(7);
        }
        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(authorization);
        String username = (String) attributes.get("username");

        return userApi.retrieve(username);
    }
}
