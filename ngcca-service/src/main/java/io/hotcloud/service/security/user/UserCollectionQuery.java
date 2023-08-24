package io.hotcloud.service.security.user;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class UserCollectionQuery {

    private final UserApi userApi;

    public UserCollectionQuery(UserApi userApi) {
        this.userApi = userApi;
    }

    /**
     * Paging query all user with the giving parameter
     *
     * @param username username
     * @param enabled  whether the user enabled
     * @param pageable {@link Pageable}
     * @return paged user collection
     */
    public PageResult<User> pagingQuery(@Nullable String username, @Nullable Boolean enabled, Pageable pageable) {
        Collection<User> users;
        if (StringUtils.hasText(username)) {
            users = userApi.usersLike(username);
        } else {
            users = userApi.users();
        }
        Collection<User> filtered = filter(users, enabled);
        return PageResult.ofCollectionPage(filtered, pageable);
    }

    public Collection<User> filter(Collection<User> users, Boolean enabled) {
        if (enabled == null) {
            return users;
        }
        return users.stream()
                .filter(e -> Objects.equals(e.isEnabled(), enabled))
                .collect(Collectors.toList());
    }
}
