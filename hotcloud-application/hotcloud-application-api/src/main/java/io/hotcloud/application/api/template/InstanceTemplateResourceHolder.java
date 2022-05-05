package io.hotcloud.application.api.template;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateResourceHolder {

    private final Map<Template, String> holder = new ConcurrentHashMap<>(32);

    public void put(Template type, String template) {
        Assert.hasText(template, "Instance template is null");
        Assert.notNull(type, "Instance template type is null");
        holder.put(type, template);
    }

    public String get(Template type) {
        Assert.notNull(type, "Instance template type is null");
        return holder.get(type);
    }
}
