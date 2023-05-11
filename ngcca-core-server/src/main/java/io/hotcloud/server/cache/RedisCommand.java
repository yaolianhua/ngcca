package io.hotcloud.server.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisCommand<K, V> {

    void ttlKey(K key, V value, TimeUnit timeUnit, long ttl);

    Set<K> listKeys();

    void lpush(K key, V value);

    void rpush(K key, V value);

    V lpop(K key);

    V rpop(K key);

    Boolean hasKey(K key);

    <HK> Set<HK> hKeys(K key);

    <HK, HV> void hSet(K key, HK hashKey, HV value);

    <HK, HV> void hmSet(K key, Map<HK, HV> map);

    <HK> Boolean hDelete(K key, HK hashKey);

    <HK> Boolean hDelete(K key, List<HK> hashKeys);

    <HK, HV> HV hGet (K key, HK hashKey);

    <HK, HV> Map<HK, HV> hmGet (K key);
}
