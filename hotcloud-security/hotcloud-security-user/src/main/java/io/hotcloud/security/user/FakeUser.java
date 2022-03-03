package io.hotcloud.security.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class FakeUser implements UserDetails {

    private String account;
    private String password;
    private String nickname;
    private boolean enabled;

    public static FakeUser of(String username, String nickname, String password) {
        FakeUser fakeUser = new FakeUser();
        fakeUser.setAccount(username);
        fakeUser.setNickname(nickname);
        fakeUser.setPassword(password);
        fakeUser.setEnabled(true);

        return fakeUser;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
