package io.hotcloud.common.cache;

import io.hotcloud.common.model.exception.PlatformException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class RedisCache extends AbstractValueAdaptingCache {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Object lookup(String key) {
        Assert.hasText(key, "Key is null");
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) {
            return null;
        }
        return fromStoreValue(o);
    }

    @Override
    protected Object toStoreValue(Object givingValue) {
        return super.toStoreValue(givingValue);
    }

    @Override
    protected Object fromStoreValue(Object storeValue) {
        return super.fromStoreValue(storeValue);
    }

    @Override
    public void put(String key, Object value) {
        Assert.hasText(key, "Key is null");
        Assert.notNull(value, "Value is null");
        redisTemplate.opsForValue().set(key, toStoreValue(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Callable<T> valueLoader) {
        Assert.hasText(key, "Key is null");
        Assert.notNull(valueLoader, "Value loader is null");

        Boolean hasKey = redisTemplate.hasKey(key);
        boolean existKey = hasKey != null && hasKey;
        if (existKey) {
            return (T) fromStoreValue(redisTemplate.opsForValue().get(key));
        }
        try {
            redisTemplate.opsForValue().set(key, toStoreValue(valueLoader.call()));
        } catch (Exception e) {
            throw new PlatformException(String.format("Value for key '%s' could not be loaded using '%s'", key, valueLoader));
        }

        return (T) get(key);
    }

    @Override
    public void evict(String key) {
        Assert.hasText(key, "Key is null");
        redisTemplate.delete(key);
        log.info("Evict key '{}'", key);
    }

    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys("*");
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
        log.info("Clear all keys");
    }
}
