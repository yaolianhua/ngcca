package io.hotcloud.service.template;

public interface TemplateInstanceProcessor {

    /**
     * @param template  {@link Template}
     * @return {@link TemplateInstance}
     */
    TemplateInstance process(Template template, TemplateVariables variables);

    boolean support(Template template);
}
