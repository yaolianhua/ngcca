package io.hotcloud.common.server.cache;

import io.hotcloud.common.api.cache.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public class CaffeineCacheTest {

    private final CacheObjectTest cacheData = new CacheObjectTest();

    @BeforeEach
    public void before() {
        cacheData.setName("My Cache");
        cacheData.setValue("My Cache Value");

        cacheData.setMap(Map.of("K1", "V1", "K2", "V2"));
        cacheData.setCacheObjectTests(List.of(new CacheObjectTest("Inner name", "Inner value")));
    }

    @Test
    public void cache() {

        Cache cache = new CaffeineCache(null);

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
