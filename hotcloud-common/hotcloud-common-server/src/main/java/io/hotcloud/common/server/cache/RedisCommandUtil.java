package io.hotcloud.common.server.cache;

import io.hotcloud.common.api.cache.RedisCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class RedisCommandUtil<K,V> implements RedisCommand<K,V> {

    private final RedisTemplate<K, V> redisTemplate;

    public RedisCommandUtil(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
