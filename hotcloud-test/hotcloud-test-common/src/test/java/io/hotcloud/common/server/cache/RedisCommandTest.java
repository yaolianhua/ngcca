package io.hotcloud.common.server.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisCommandTest {

    private final CacheObjectTest cacheData = new CacheObjectTest();

    @BeforeEach
    public void before() {
        cacheData.setName("My Cache");
        cacheData.setValue("My Cache Value");

        cacheData.setMap(Map.of("K1", "V1", "K2", "V2"));
        cacheData.setCacheObjectTests(List.of(new CacheObjectTest("Inner name", "Inner value")));
    }

    @Disabled
    @Test
    public void redisCommand() {

        RedisConnectionHelper.ConnectionValidBind connectionValidBind = RedisConnectionHelper.isValidConnection("localhost", 6379, "QbMufCD@9WVQ^Hv", 14);
        Assertions.assertTrue(connectionValidBind.isValid());

        RedisTemplate<String, Object> redisTemplate = RedisConnectionHelper.getRedisTemplate(connectionValidBind.getRedisConnectionFactory());

        RedisCommandUtil<String, Object> commandUtil = new RedisCommandUtil<>(redisTemplate);

        commandUtil.hSet("H-KEY", "MK1", "MV1");
        commandUtil.hmSet("HM-KEY", cacheData.getMap());

        Set<String> hKeys = commandUtil.hKeys("H-KEY");
        Assertions.assertEquals(1, hKeys.size());

        Set<String> hmKeys = commandUtil.hKeys("HM-KEY");
        Assertions.assertEquals(2, hmKeys.size());

        String mv1 = commandUtil.hGet("H-KEY", "MK1");
        Assertions.assertEquals("MV1", mv1);

        Map<String, String> hmGet = commandUtil.hmGet("HM-KEY");
        Assertions.assertEquals(cacheData.getMap(), hmGet);

        Boolean hDelete = commandUtil.hDelete("H-KEY", "MK1");
        Assertions.assertTrue(hDelete);

        Boolean hmDelete = commandUtil.hDelete("HM-KEY", List.of("K1", "K2"));
        Assertions.assertTrue(hmDelete);


    }
}
