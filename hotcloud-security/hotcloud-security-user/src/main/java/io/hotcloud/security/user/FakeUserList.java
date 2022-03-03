package io.hotcloud.security.user;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class FakeUserList {

    private List<FakeUser> items = new LinkedList<>();

    public void add(FakeUser fakeUser) {
        items.add(fakeUser);
    }
}
