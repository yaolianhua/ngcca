package io.hotcloud.application.api.template;

public interface InstanceTemplateProcessor {

    InstanceTemplate process(Template template, String user, String namespace);

    boolean support (Template template);
}
