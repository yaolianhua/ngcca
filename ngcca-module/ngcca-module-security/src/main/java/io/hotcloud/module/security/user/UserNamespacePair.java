package io.hotcloud.module.security.user;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class UserNamespacePair {
    private String username;
    private String namespace;

    public UserNamespacePair(String username, String namespace) {
        this.username = username;
        this.namespace = namespace;
    }

}
