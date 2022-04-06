package io.hotcloud.common.util;

import io.hotcloud.common.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class Base64Helper {

    private Base64Helper() {
    }

    public static String encode(String plain) {
        Assert.hasText(plain, "plain text is null", 400);
        return Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String base64) {
        Assert.hasText(base64, "base64 text is null", 400);
        return new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    }

    public static String encodeUrl(String plainUrl) {
        Assert.hasText(plainUrl, "plain url text is null", 400);
        return Base64.getUrlEncoder().encodeToString(plainUrl.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeUrl(String encodedUrl) {
        Assert.hasText(encodedUrl, "encoded url is null", 400);
        return new String(Base64.getUrlDecoder().decode(encodedUrl), StandardCharsets.UTF_8);
    }

    //{"auths":{"harbor.example.cn":{"username":"admin","password":"Harbor12345","auth":"YWRtaW46SGFyYm9yMTIzNDU="}}}
    public static String dockerconfigjson(String registry, String username, String password) {
        Assert.hasText(registry, "registry is null", 400);
        Assert.hasText(username, "username is null", 400);
        Assert.hasText(password, "password is null", 400);

        String auth = encode(username + ":" + password);
        String template = "{\"auths\":{\"" + registry + "\":{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"auth\":\"" + auth + "\"}}}";
        return template;
    }

}
