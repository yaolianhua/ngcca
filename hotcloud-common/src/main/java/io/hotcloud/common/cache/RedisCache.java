package io.hotcloud.common.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class RedisCache extends AbstractValueAdaptingCache {

    @Override
    protected Object lookup(String key) {
        return null;
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

    }

    @Override
    public <T> T get(String key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void evict(String key) {

    }

    @Override
    public void clear() {

    }
}
