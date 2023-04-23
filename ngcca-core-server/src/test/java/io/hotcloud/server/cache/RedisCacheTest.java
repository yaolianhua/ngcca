package io.hotcloud.server.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public class RedisCacheTest {

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
    public void cache() {

        RedisConnectionHelper.ConnectionValidBind connectionValidBind = RedisConnectionHelper.isValidConnection("localhost", 6379, "QbMufCD@9WVQ^Hv", 15);
        Assertions.assertTrue(connectionValidBind.isValid());

        RedisTemplate<String, Object> redisTemplate = RedisConnectionHelper.getRedisTemplate(connectionValidBind.getRedisConnectionFactory());

        Cache cache = new RedisCache(redisTemplate);

        cache.put("C1", cacheData);
        Assertions.assertNotNull(cache.get("C1"));

        CacheObjectTest cacheObjectTest = cache.get("C1", CacheObjectTest.class);
        Assertions.assertEquals(cacheData, cacheObjectTest);

        CacheObjectTest cachedObject_ = cache.get("C1", CacheObjectTest::new);
        Assertions.assertEquals(cacheData, cachedObject_);

        CacheObjectTest c2 = cache.get("C2", CacheObjectTest::new);
        Assertions.assertNotNull(c2);

        Object ifAbsent = cache.putIfAbsent("C1", "New Value");
        Assertions.assertNotEquals("New Value", ifAbsent);
        Assertions.assertEquals(cacheData, ifAbsent);

        cache.evict("C1");
        Assertions.assertNull(cache.get("C1"));

        cache.clear();
        Assertions.assertNull(cache.get("C2"));

    }

}
