package io.hotcloud.security.admin;

import io.hotcloud.security.admin.jwt.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class JwtTest {

    @Test
    public void sign() {
        JwtSigner jwtSigner = new JwtManager();
        Map<String, Object> data = Map.of(
                "username", "admin",
                "nickname", "administrator",
                "locked", false,
                "permissions", List.of("admin","user","guest"));
        String sign = jwtSigner.sign(new JwtBody(data));
        log.info("Sign: \n {}", sign);

        String sign2 = jwtSigner.sign(data);
        log.info("Sign2: \n {}", sign2);
    }

    @Test
    public void verify(){
        JwtVerifier jwtVerifier = new JwtManager();
        Jwt jwt = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBcGkgQXV0aCIsImF1ZCI6WyJDbGllbnQiLCJXZWIiXSwicGVybWlzc2lvbnMiOlsiYWRtaW4iLCJ1c2VyIiwiZ3Vlc3QiXSwiaXNzIjoiSG90IENsb3VkIiwibmlja25hbWUiOiJhZG1pbmlzdHJhdG9yIiwibG9ja2VkIjpmYWxzZSwiaWF0IjoxNjQ2MjI1Mjg1LCJqdGkiOiI1YzU3MmJhZS1mZWYxLTRmOGEtOTdiYi03MjQzNzA1YjVmZjMiLCJ1c2VybmFtZSI6ImFkbWluIn0.k9Q5PxqgqqKtANPgTxRf-ioF-6aGzUwDEpsowFWuWelchm06VUEqXShRWFWZqfqDmcbrnNTHgK9IK9mfXCKfdg");

        Map<String, Object> attributes = jwt.payload().getAttributes();
        Assertions.assertFalse(attributes.isEmpty());
        Assertions.assertEquals( "admin",attributes.get("username"));
        Assertions.assertFalse(((boolean) attributes.get("locked")));
        Assertions.assertEquals( List.of("admin","user","guest"), attributes.get("permissions"));

        String encodedSignSecret = Base64.getEncoder().encodeToString(Jwt.SECRET.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(encodedSignSecret, jwt.signKeySecret());
    }
}
