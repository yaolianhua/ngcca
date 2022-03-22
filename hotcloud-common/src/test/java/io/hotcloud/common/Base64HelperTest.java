package io.hotcloud.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author yaolianhua789@gmail.com
 **/
public class Base64HelperTest {

    @Test
    public void encode() {
        String plain = "{\"auths\":{\"harbor.cloud2go.cn\":{\"username\":\"admin\",\"password\":\"Harbor12345\",\"auth\":\"YWRtaW46SGFyYm9yMTIzNDU=\"}}}";

        String encode = Base64Helper.encode(plain);
        Assertions.assertEquals("eyJhdXRocyI6eyJoYXJib3IuY2xvdWQyZ28uY24iOnsidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiSGFyYm9yMTIzNDUiLCJhdXRoIjoiWVdSdGFXNDZTR0Z5WW05eU1USXpORFU9In19fQ==", encode);
    }

    @Test
    public void decode() {
        String plain = "{\"auths\":{\"harbor.cloud2go.cn\":{\"username\":\"admin\",\"password\":\"Harbor12345\",\"auth\":\"YWRtaW46SGFyYm9yMTIzNDU=\"}}}";

        String decode = Base64Helper.decode("eyJhdXRocyI6eyJoYXJib3IuY2xvdWQyZ28uY24iOnsidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiSGFyYm9yMTIzNDUiLCJhdXRoIjoiWVdSdGFXNDZTR0Z5WW05eU1USXpORFU9In19fQ==");
        Assertions.assertEquals(plain, decode);
    }

    @Test
    public void encodeDockerConfig() {
        String dockerConfig = Base64Helper.encodeDockerConfig("harbor.cloud2go.cn", "admin", "Harbor12345");
        Assertions.assertEquals("eyJhdXRocyI6eyJoYXJib3IuY2xvdWQyZ28uY24iOnsidXNlcm5hbWUiOiJhZG1pbiIsInBhc3N3b3JkIjoiSGFyYm9yMTIzNDUiLCJhdXRoIjoiWVdSdGFXNDZTR0Z5WW05eU1USXpORFU9In19fQ==", dockerConfig);
    }
}
