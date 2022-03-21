package io.hotcloud.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.support.SerializationDelegate;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class CaffeineCache extends AbstractValueAdaptingCache {

    private final Cache<String, Object> cache = Caffeine.newBuilder().build();
    private final SerializationDelegate serializationDelegate;

    public CaffeineCache(@Nullable SerializationDelegate serializationDelegate) {
        this.serializationDelegate = serializationDelegate;
        log.info("CaffeineCache init");
    }

    @Override
    protected synchronized Object lookup(String key) {
        Assert.hasText(key, "Key is null", 400);
        if (cache.getIfPresent(key) == null) {
            return null;
        }
        return fromStoreValue(cache.getIfPresent(key));
    }

    @Override
    public synchronized void put(String key, Object value) {
        Assert.hasText(key, "Key is null", 400);
        Assert.notNull(value, "Value is null", 400);
        cache.put(key, toStoreValue(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T> T get(String key, Callable<T> valueLoader) {
        Assert.hasText(key, "Key is null", 400);
        Assert.notNull(valueLoader, "Value loader is null", 400);
        return ((T) fromStoreValue(cache.get(key, v -> {
            try {
                return toStoreValue(valueLoader.call());
            } catch (Exception e) {
                throw new HotCloudException(String.format("Value for key '%s' could not be loaded using '%s'", key, valueLoader));
            }
        })));

    }

    @Override
    public synchronized void evict(String key) {
        Assert.hasText(key, "Key is null", 400);
        cache.invalidate(key);
        log.info("Evict key '{}'", key);
    }

    @Override
    public synchronized void clear() {
        cache.invalidateAll();
        log.info("Clear all keys");
    }

    @Override
    protected Object toStoreValue(Object givingValue) {
        Assert.notNull(givingValue, "Giving value is null", 400);
        Object storeValue = super.toStoreValue(givingValue);

        if (this.serializationDelegate != null) {
            try {
                return this.serializationDelegate.serializeToByteArray(storeValue);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + givingValue +
                        "'. Does it implement Serializable?", ex);
            }
        } else {
            return storeValue;
        }


    }

    @Override
    protected Object fromStoreValue(Object storeValue) {
        Assert.notNull(storeValue, "Store value is null", 400);
        if (this.serializationDelegate != null) {
            try {
                return super.fromStoreValue(this.serializationDelegate.deserializeFromByteArray((byte[]) storeValue));
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        } else {
            return super.fromStoreValue(storeValue);
        }

    }
}
