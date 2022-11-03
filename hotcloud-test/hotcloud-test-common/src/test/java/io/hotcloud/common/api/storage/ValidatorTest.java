package io.hotcloud.common.api.storage;

import io.hotcloud.common.api.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
public class ValidatorTest {

    static Stream<String> invalidIPv4Provider() {
        return Stream.of(
                "000.000.000.000",          // leading 0
                "00.00.00.00",              // leading 0
                "1.2.3.04",                 // leading 0
                "1.02.03.4",                // leading 0
                "1.2",                      // 1 dot
                "1.2.3",                    // 2 dots
                "1.2.3.4.5",                // 4 dots
                "192.168.1.1.1",            // 4 dots
                "256.1.1.1",                // 256
                "1.256.1.1",                // 256
                "1.1.256.1",                // 256
                "1.1.1.256",                // 256
                "-100.1.1.1",               // -100
                "1.-100.1.1",               // -100
                "1.1.-100.1",               // -100
                "1.1.1.-100",               // -100
                "1...1",                    // empty between .
                "1..1",                     // empty between .
                "1.1.1.1."                  // last .
        );                          // empty
    }

    static Stream<String> validIPv4Provider() {
        return Stream.of(
                "0.0.0.0",
                "0.0.0.1",
                "127.0.0.1",
                "1.2.3.4",              // 0-9
                "11.1.1.0",             // 10-99
                "101.1.1.0",            // 100-199
                "201.1.1.0",            // 200-249
                "255.255.255.255",      // 250-255
                "192.168.1.1",
                "192.168.1.255",
                "100.100.100.100");
    }

    @Test
    public void inetAddress() {
        invalidIPv4Provider().forEach(e -> Assertions.assertFalse(Validator.validIpv4(e)));
        validIPv4Provider().forEach(e -> Assertions.assertTrue(Validator.validIpv4(e)));
    }

    @Test
    public void gitAddress() {
        Assertions.assertTrue(Validator.validHTTPGitAddress("https://github.com/GoogleContainerTools/kaniko.git"));
        Assertions.assertTrue(Validator.validHTTPGitAddress("http://github.com/GoogleContainerTools/kaniko.git"));
        Assertions.assertFalse(Validator.validHTTPGitAddress("git@github.com:GoogleContainerTools/kaniko.git"));
        Assertions.assertTrue(Validator.validHTTPGitAddress("https://git.docker.local/self-host/thymeleaf-fragments.git"));
    }

    static Stream<String> validUsernameProvider() {
        return Stream.of(
                "admin",
                "jason",
                "smith123",
                "jasonstanth"
        );
    }

    static Stream<String> invalidUsernameProvider() {
        return Stream.of(
                "jack",      //length
                "adminjasonsmihthy", //length
                "Admin",
                "jason@",
                "123smith123",
                "admin.123"
        );
    }

    @Test
    public void username() {
        validUsernameProvider().forEach(e -> Assertions.assertTrue(Validator.validUsername(e)));
        invalidUsernameProvider().forEach(e -> Assertions.assertFalse(Validator.validUsername(e)));
    }

    static Stream<String> validK8sNameProvider() {
        return Stream.of(
                "admin",
                "jason-hello",
                "smith12344444444444444444444444444444444",
                "jason12354stanth",
                "123abcdefg"
        );
    }

    static Stream<String> invalidK8sNameProvider() {
        return Stream.of(
                "",
                "Jack",
                "-123adminjasonsmihthy",
                "admin_",
                "jason@",
                "smith123-",
                "admin.123",
                "admin管理员"
        );
    }
    @Test
    public void k8sName(){
        validK8sNameProvider().forEach(e -> Assertions.assertTrue(Validator.validK8sName(e)));
        invalidK8sNameProvider().forEach(e -> Assertions.assertFalse(Validator.validK8sName(e)));
    }

}
