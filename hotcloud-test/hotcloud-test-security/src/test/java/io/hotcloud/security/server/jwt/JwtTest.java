package io.hotcloud.security.server.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class JwtTest {

    @Test
    public void signNoExpiration() {
        JwtSigner jwtSigner = new JwtManager(null);
        Map<String, Object> data = Map.of(
                "username", "admin",
                "nickname", "administrator",
                "locked", false,
                "permissions", List.of("admin", "user", "guest"));
        String sign = jwtSigner.sign(new JwtBody(data, null, null));
        log.info("Sign: \n {}", sign);

        String sign2 = jwtSigner.sign(data);
        log.info("Sign2: \n {}", sign2);
    }

    @Test
    public void signWithExpiration_then_verification() throws InterruptedException {
        JwtSigner jwtSigner = new JwtManager(null);
        Map<String, Object> data = Map.of(
                "username", "admin",
                "nickname", "administrator",
                "locked", false,
                "permissions", List.of("admin", "user", "guest"));
        String sign = jwtSigner.signExpiration(data, TimeUnit.SECONDS, 1);

        JwtVerifier jwtVerifier = new JwtManager(null);
        Map<String, Object> attributes = jwtVerifier.retrieveAttributes(sign);

        Assertions.assertEquals("admin", attributes.get("username"));
        Assertions.assertFalse(((boolean) attributes.get("locked")));
        Assertions.assertEquals(List.of("admin", "user", "guest"), attributes.get("permissions"));

        TimeUnit.SECONDS.sleep(2);
        Assertions.assertFalse(jwtVerifier.valid(sign));
    }

    @Test
    public void verify() {
        JwtVerifier jwtVerifier = new JwtManager(null);
        Jwt jwt = jwtVerifier.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBcGkgQXV0aCIsImF1ZCI6WyJDbGllbnQiLCJXZWIiXSwicGVybWlzc2lvbnMiOlsiYWRtaW4iLCJ1c2VyIiwiZ3Vlc3QiXSwiaXNzIjoiSG90IENsb3VkIiwibmlja25hbWUiOiJhZG1pbmlzdHJhdG9yIiwibG9ja2VkIjpmYWxzZSwiaWF0IjoxNjQ2MjI1Mjg1LCJqdGkiOiI1YzU3MmJhZS1mZWYxLTRmOGEtOTdiYi03MjQzNzA1YjVmZjMiLCJ1c2VybmFtZSI6ImFkbWluIn0.k9Q5PxqgqqKtANPgTxRf-ioF-6aGzUwDEpsowFWuWelchm06VUEqXShRWFWZqfqDmcbrnNTHgK9IK9mfXCKfdg");

        Map<String, Object> attributes = jwt.payload().getAttributes();
        Assertions.assertFalse(attributes.isEmpty());
        Assertions.assertEquals("admin", attributes.get("username"));
        Assertions.assertFalse(((boolean) attributes.get("locked")));
        Assertions.assertEquals( List.of("admin","user","guest"), attributes.get("permissions"));

    }
}
