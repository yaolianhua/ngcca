package io.hotcloud.server.cache;

import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public abstract class AbstractValueAdaptingCache implements Cache {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> type) {
        Assert.hasText(key, "Key is null");
        Assert.notNull(type, "Class type is null");
        Object value = lookup(key);
        if (Objects.isNull(value)){
            return null;
        }
        Assert.state(type.isInstance(value), "Cached value is not of required type [" + type.getName() + "]: " + value);

        return ((T) value);
    }

    @Override
    public Object get(String key) {
        return lookup(key);
    }

    /**
     * Perform an actual lookup in the underlying store
     *
     * @param key the key whose associated value is to be returned
     * @return the raw store value for the key
     */
    protected abstract Object lookup(String key);

    /**
     * Convert the given user value, as passed into the put method, to a value in the internal store
     *
     * @param givingValue the giving value
     * @return the value to store
     */
    protected Object toStoreValue(Object givingValue) {
        Assert.notNull(givingValue, "Giving value is null");
        return givingValue;
    }

    /**
     * Convert the given value from the internal store to a user value returned from the get method
     *
     * @param storeValue the store value
     * @return the value to return to the user
     */
    protected Object fromStoreValue(Object storeValue) {
        Assert.notNull(storeValue, "Store value is null");
        return storeValue;
    }

}
