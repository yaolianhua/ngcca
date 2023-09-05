package io.hotcloud.kubernetes.server;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class KubernetesClusterApiBeanManager {

    private static final Map<String, Set<Object>> BEANS = new ConcurrentHashMap<>(32);

    public synchronized void addBean(String id, Object bean) {
        Set<Object> beans = BEANS.get(id);
        if (beans == null || beans.isEmpty()) {
            beans = new HashSet<>();
        }

        beans.add(bean);
        BEANS.put(id, beans);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getBean(String id, Class<T> beanClass) {
        Set<Object> beans = BEANS.get(id);
        if (beans == null || beans.isEmpty()) {
            return null;
        }

        Object bean = beans.stream().filter(beanClass::isInstance)
                .findFirst()
                .orElse(null);

        return Objects.isNull(bean) ? null : (T) bean;

    }

}
