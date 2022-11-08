package io.hotcloud.security.autoconfigure.jwt;

import io.hotcloud.security.api.jwt.RSAHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

/**
 * @author yaolianhua789@gmail.com
 **/
public class RSAHelperTest {


    private final String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDGYOEufsE0zbr8vzoFtuHzpJdjNbI7qeC2n/FFeEWQ3IR3GLnHi20wngJE/w9gJ8E+ZHImfsYMO0dL9Pw1" +
            "JVzEZF9AO8pycoVGh03b/7mCHvX4Jo7g0gaLBbOPPQ5Ya85DNH3ZwU8aPemb1jkXydHuBGFchLvyJTMcB8Mc6uesnJbjF1k/u+RPGf5a45grZrSWstB5UD+nr7PaQorkgb/0aMuFI" +
            "yBvWbk3Hi4ymtDy7Q1qUAVBlAt7st69nXoKjL18b9GEvJ3nfyznsyV+t7jX34G7dFgU2Ej6vP/kCAOz1OvI/xBWmLvUaLK9Ze18ocpaZD+a/tLClmcTk77m/OkzAgMBAAECggEAIzsbYr2C" +
            "JJWQ6sgvTsp+8F0/wY/Muond7bEUEUld2W9AW0JZ5BVf6+1z67r8iRAmYqVGHI4RMCTq8ZNtoyaihgJpZCf6OT8baj4nVrXgxTooZsy6PTUvkksN65dlT9C9jfPpkd2h5cAvF80A0Le3/7Rpchv" +
            "koQl4PTcnsKALSv0oR3B9kjStDNwmLb4C4Tm98MTmN5msBedhywgN8gox3hLhIMah2/KOoaiFMV75tIiHlS5ZRHSojWv1SmJEnUYIwMRsyb+cuuUAmMnK+Q0IDQwxriNnn4hCGjxu6pASFe3D7Z1uk0" +
            "Yi2kuzpTe8ZiuJ1e3sr2hcsLmzdUXK5+HzIQKBgQDhYBA9uz/YB6/d/c1z1wRRL148yh9j2ZMftZFnPjak/6ZxVRpAM1xhp3JF8lIpYYtFIUnhxeySSBt0WFEdwjISvvuVS2VGmqykumBhqnHA0UflO09yrZnm" +
            "SOdPmG9g5zbe6tb4L4aAn0iJ8f0RFOTi3a0tJh2gKq+Q2JeHPQvh8QKBgQDhVbN7cciKfx+g4gUrie2zeetw5jPsmL4pJoVZAeZIPOiNnpvga2bW4riq6M5RDOD6HD5euEvMTARGeGZQjCDoTvga63KxOgAjiE7gi" +
            "T+LR9iXMUSj4auqMIlGTSh9tzgxygBSu1iRjOrjgxTkdRy8hUbGJgbI8wpa0SPCs5QZYwKBgBqwdpvo3dGmb2Q5lyf9sYwvsNhAE2GOtYaOyO5SBsSZZcuMCXTK4EjMPJMd9C7kV0rTe11aQ/66vU2U295LTgS8SyzN5e" +
            "tri8BiNeG6/oeYAQybbnglKNGFlqbtZVszZmMVqmlvr1zx//QeDRsFv2lCWSy8k8uQ4oOjsqYYS5zxAoGBALv91+OhJgIF1MlQR6fvRpR5nudt5tv5Ao0qSg3b+9dAmXOtY7ZzZDTaqd5Mo/QSno+LiK1VbEdhFCAGyrVgcwP" +
            "c/jjXPbSGAx+h1MyXNKBqrFRXEkD3QVSXca9pJ+LaPNOtB1/+VQH5Yu20qnV/Dx5owocFqptzyKvD1+XUFDapAoGBAKH+OgUPu5Yq3U7gess4ZFD/z0f1dhrstiVEI8PD9mKR8C7q0" +
            "cE6MyHLl092emBK9Nat4zlQPXQaAkgIPIlj3kUskCYXPkOCc9epHmKyEWSuA2W6VWC5D8d60LYJotTe/8ms2zHOrx1wl1GPP6jBvlTw4ZObq2N38UssYc2GfojR";
    private final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxmDhLn7BNM26/L86Bbbh86SXYzWyO6ngtp/xRXhFkNyEdxi5x4ttMJ4CRP8PYCfBPmRyJn7GDDtHS/T8NSVcxGRfQDvKcnKFRod" +
            "N2/+5gh71+CaO4NIGiwWzjz0OWGvOQzR92cFPGj3pm9Y5F8nR7gRhXIS78iUzHAfDHOrnrJyW4xdZP7vkTxn+WuOYK2a0lrLQeVA/p6+z2kKK5IG/9GjLhSMgb1m5Nx4uMprQ8u0NalAFQZQLe7LevZ16Coy9f" +
            "G/RhLyd538s57Mlfre419+Bu3RYFNhI+rz/5AgDs9TryP8QVpi71GiyvWXtfKHKWmQ/mv7SwpZnE5O+5vzpMwIDAQAB";

    @Test
    public void sign_verify() throws Exception {
        byte[] encodeBytes = Base64.getEncoder().encode("Hello RSA".getBytes());
        String sign = RSAHelper.sign(encodeBytes, privateKey);
        boolean verify = RSAHelper.verify(encodeBytes, publicKey, sign);
        Assertions.assertTrue(verify);
    }

    @Test
    public void encrypt_decrypt() throws Exception {
        byte[] originBytes = "Hello RSA".getBytes();
        byte[] encryptByPublicKeyBytes = RSAHelper.encryptByPublicKey(originBytes, publicKey);
        byte[] decryptByPrivateKeyBytes = RSAHelper.decryptByPrivateKey(encryptByPublicKeyBytes, privateKey);

        Assertions.assertArrayEquals(decryptByPrivateKeyBytes, originBytes);
    }
}
