package io.hotcloud.application.api.template;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateResourceManager {

    private final Map<Template, String> container = new ConcurrentHashMap<>(32);

    public void put(Template type, String template) {
        Assert.hasText(template, "Instance template is null");
        Assert.notNull(type, "Instance template type is null");
        container.put(type, template);
    }

    public String get(Template type) {
        Assert.notNull(type, "Instance template type is null");
        return container.get(type);
    }
}
