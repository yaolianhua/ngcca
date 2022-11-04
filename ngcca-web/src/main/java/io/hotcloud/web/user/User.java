package io.hotcloud.web.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class User {
    private String id;
    private String username;
    private String password;
    private String nickname;

    private String mobile;
    private String email;

    private boolean enabled;

    private String avatar;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
