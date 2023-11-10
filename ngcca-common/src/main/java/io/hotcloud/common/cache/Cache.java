package io.hotcloud.common.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface Cache {

    /**
     * Associate the specified value with the specified key in this cache.
     * <p>If the cache previously contained a mapping for this key, the old
     * value is replaced by the specified value
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    void put(String key, Object value);

    default void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    /**
     * Atomically associate the specified value with the specified key in this cache
     * if it is not set already
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the value to which this cache maps the specified key
     */
    default Object putIfAbsent(String key, Object value) {
        final Object existingValue = get(key);
        if (existingValue == null) {
            put(key, value);
        }
        return existingValue;
    }

    /**
     * Return the value to which this cache maps the specified key,
     * generically specifying a type that return value will be cast to.
     *
     * @param key  the key whose associated value is to be returned
     * @param type the required type of the returned value
     * @param <T>  type of class
     * @return the value to which this cache maps the specified key
     */
    <T> T get(String key, Class<T> type);

    /**
     * Return the value to which this cache maps the specified key
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which this cache maps the specified key
     */
    Object get(String key);

    /**
     * Return the value to which this cache maps the specified key, obtaining
     * that value from {@code valueLoader} if necessary. This method provides
     * a simple substitute for the conventional "if cached, return; otherwise
     * create, cache and return" pattern
     * <p>If possible, implementations should ensure that the loading operation
     * is synchronized so that the specified {@code valueLoader} is only called
     * once in case of concurrent access on the same key.
     *
     * @param key         the key whose associated value is to be returned
     * @param <T>         type of class
     * @param valueLoader new value will be load by this callable
     * @return the value to which this cache maps the specified key
     */
    <T> T get(String key, Callable<T> valueLoader);

    /**
     * Evict the mapping for this key from this cache if it is present
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    void evict(String key);

    /**
     * Clear the cache through removing all mappings
     */
    void clear();
}
