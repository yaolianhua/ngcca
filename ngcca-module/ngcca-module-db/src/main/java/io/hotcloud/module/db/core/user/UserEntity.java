package io.hotcloud.module.db.core.user;

import io.hotcloud.module.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "users")
@Getter
@Setter
public class UserEntity extends AbstractEntity {

    @Indexed(unique = true)
    private String username;
    private String password;

    private String nickname;
    private String mobile;
    private String email;

    private boolean enabled;

    private String avatar;
}
