package io.hotcloud.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisCommandUtil<K,V> implements RedisCommand<K,V> {

    private final RedisTemplate<K, V> redisTemplate;

    public RedisCommandUtil(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void set(K key, V value, TimeUnit timeUnit, long ttl) {
        if (Objects.isNull(timeUnit)) {
            redisTemplate.opsForValue().set(key, value);
            return;
        }
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(K key, Class<T> type) {
        Object v = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(type)) {
            return (T) v;
        }
        Assert.state(type.isInstance(v), "redis cached value is not of required type [" + type.getName() + "]: " + v);
        return ((T) v);
    }

    @Override
    public Boolean delete(K key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void ttlKey(K key, V value, TimeUnit timeUnit, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> listKeys() {
        return redisTemplate.keys((K) "*");
    }

    @Override
    public void lpush(K key, V value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void rpush(K key, V value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public V lpop(K key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public V rpop(K key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public Boolean hasKey(K key) {
        return redisTemplate.hasKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <HK> Set<HK> hKeys(K key) {
        return (Set<HK>) redisTemplate.opsForHash().keys(key);
    }

    @Override
    public <HK, HV> void hSet(K key, HK hashKey, HV value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public <HK, HV> void hmSet(K key, Map<HK, HV> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public <HK> Boolean hDelete(K key, HK hashKey) {
        Long delete = redisTemplate.opsForHash().delete(key, hashKey);

        return Objects.equals(1L, delete);
    }

    @Override
    public <HK> Boolean hDelete(K key, List<HK> hashKeys) {
        if (CollectionUtils.isEmpty(hashKeys)) {
            return false;
        }
        Object[] objects = new Object[hashKeys.size()];
        for (int i = 0; i < hashKeys.size(); i++) {
            objects[i] = hashKeys.get(i);
        }

        Long delete = redisTemplate.opsForHash().delete(key, objects);
        return Objects.equals(((long) hashKeys.size()), delete);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <HK, HV> HV hGet(K key, HK hashKey) {
        return (HV) redisTemplate.opsForHash().get(key, hashKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <HK, HV> Map<HK, HV> hmGet(K key) {
        Object result = redisTemplate.opsForHash().entries(key);
        return (Map<HK, HV>) result;
    }
}
