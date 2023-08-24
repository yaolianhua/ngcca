package io.hotcloud.service.security.login;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class BearerToken {
    private String authorization;
    private String type = "Bearer";

    public static BearerToken of(String authorization) {
        BearerToken bearerToken = new BearerToken();

        bearerToken.setAuthorization(authorization);
        return bearerToken;
    }
}
