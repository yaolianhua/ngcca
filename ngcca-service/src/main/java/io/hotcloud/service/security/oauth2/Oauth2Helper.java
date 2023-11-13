package io.hotcloud.service.security.oauth2;


import com.github.javafaker.Faker;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserSocialSource;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Objects;

public class Oauth2Helper {

    private Oauth2Helper() {
    }

    public static String obtainGithubUsernameOrGenerate(OAuth2User oAuth2User) {
        Object login = oAuth2User.getAttribute("login");
        Object id = oAuth2User.getAttribute("id");

        if (Objects.nonNull(login)) {
            return String.valueOf(login) + id;
        }
        Faker faker = new Faker();
        String usernameWithDot = faker.name().username();
        return usernameWithDot.replace(".", "") + id;
    }

    public static String obtainGitlabUsernameOrGenerate(OAuth2User oAuth2User) {
        Object login = oAuth2User.getAttribute("username");
        Object id = oAuth2User.getAttribute("id");

        if (Objects.nonNull(login)) {
            return String.valueOf(login) + id;
        }
        Faker faker = new Faker();
        String usernameWithDot = faker.name().username();
        return usernameWithDot.replace(".", "") + id;
    }

    public static User buildGithubUser(OAuth2User oAuth2User) {

        Object emailObject = oAuth2User.getAttribute("email");
        Object nicknameObject = oAuth2User.getAttribute("name");
        Object avatarObject = oAuth2User.getAttribute("avatar_url");
        Object id = oAuth2User.getAttribute("id");

        return User.builder()
                .id(String.valueOf(id))
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

    public static User buildGitlabUser(OAuth2User oAuth2User) {

        Object emailObject = oAuth2User.getAttribute("email");
        Object nicknameObject = oAuth2User.getAttribute("name");
        Object avatarObject = oAuth2User.getAttribute("avatar_url");
        Object id = oAuth2User.getAttribute("id");

        return User.builder()
                .id(String.valueOf(id))
                .social(UserSocialSource.GITLAB)
                .enabled(true)
                .createdAt(Instant.now())
                .username(obtainGitlabUsernameOrGenerate(oAuth2User))
                .avatar(Objects.isNull(avatarObject) ? null : String.valueOf(avatarObject))
                .nickname(Objects.isNull(nicknameObject) ? null : String.valueOf(nicknameObject))
                .email(Objects.isNull(emailObject) ? null : String.valueOf(emailObject))
                .password(UserSocialSource.DEFAULT_PASSWORD)
                .build();
    }
}
