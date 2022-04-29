package io.hotcloud.application.api.template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yaolianhua789@gmail.com
 **/
public class InstanceTemplateResourceHolder {

    private final Map<Template, String> holder = new ConcurrentHashMap<>(32);

    public void put(Template type, String template) {
        holder.put(type, template);
    }

    public String get(Template type) {
        return holder.get(type);
    }
}
