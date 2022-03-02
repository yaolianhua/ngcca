package io.hotcloud.security.admin;

import io.hotcloud.security.admin.jwt.JwtBody;
import io.hotcloud.security.admin.jwt.JwtManager;
import io.hotcloud.security.admin.jwt.JwtSigner;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class JwtTest {

    @Test
    public void sign() {
        JwtSigner jwtSigner = new JwtManager();
        Map<String, Object> data = Map.of("username", "admin",
                "nickname", "管理员",
                "locked", false);
        String sign = jwtSigner.sign(new JwtBody(data));
        log.info("Sign: \n {}", sign);
    }
}
