package io.hotcloud.common.api.cache.task;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaskContext {

    private String id;
    private Map<String, Object> containers = new HashMap<>(128);

    public void put (String key, Object value) {
        containers.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault (String key, T defaultValue) {
        return (T) containers.getOrDefault(key, defaultValue);
    }

    public String getString (String key){
        Object v = containers.get(key);
        return null == v ? null : v.toString();
    }

    public Integer getInt (String key) {
        Object v = containers.get(key);
        return null == v ? null : ((Integer) v);
    }

    public Boolean getBoolean (String key) {
        Object v = containers.get(key);
        return null == v ? null : ((Boolean) v);
    }
}
