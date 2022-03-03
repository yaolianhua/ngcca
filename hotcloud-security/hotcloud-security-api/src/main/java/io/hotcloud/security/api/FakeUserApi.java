package io.hotcloud.security.api;

import io.hotcloud.security.user.FakeUser;
import io.hotcloud.security.user.FakeUserList;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface FakeUserApi {

    FakeUser retrieve(String username);

    FakeUserList users();
}
