package io.hotcloud.security.server.jwt;

import lombok.Data;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class SecretKeyPair {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    private SecretKeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public static SecretKeyPair of(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        return new SecretKeyPair(publicKey, privateKey);
    }

    public String getPublicKeyString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getPrivateKeyString() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}
