package io.hotcloud.service.security.oauth2;


import com.github.javafaker.Faker;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserSocialSource;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Objects;

public class Oauth2Helper {

    public static String obtainGithubUsernameOrGenerate(OAuth2User oAuth2User) {
        Object login = oAuth2User.getAttribute("login");

        if (Objects.nonNull(login)) {
            return String.valueOf(login);
        }
        Faker faker = new Faker();
        String usernameWithDot = faker.name().username();
        return usernameWithDot.replaceAll("\\.", "");
    }

    public static User buildGithubUser(OAuth2User oAuth2User) {

        Object emailObject = oAuth2User.getAttribute("email");
        Object nicknameObject = oAuth2User.getAttribute("name");
        Object avatarObject = oAuth2User.getAttribute("avatar_url");

        return User.builder()
                .social(UserSocialSource.GITHUB)
                .enabled(true)
                .createdAt(Instant.now())
                .username(obtainGithubUsernameOrGenerate(oAuth2User))
                .avatar(Objects.isNull(avatarObject) ? null : String.valueOf(avatarObject))
                .nickname(Objects.isNull(nicknameObject) ? null : String.valueOf(nicknameObject))
                .email(Objects.isNull(emailObject) ? null : String.valueOf(emailObject))
                .password(UserSocialSource.DEFAULT_PASSWORD)
                .build();
    }
}
