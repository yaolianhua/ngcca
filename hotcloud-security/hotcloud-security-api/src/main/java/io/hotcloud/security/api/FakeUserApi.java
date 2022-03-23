package io.hotcloud.security.api;

import io.hotcloud.security.user.FakeUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface FakeUserApi extends UserApi {

    FakeUser retrieve(String username);

    Collection<UserDetails> users();
}
