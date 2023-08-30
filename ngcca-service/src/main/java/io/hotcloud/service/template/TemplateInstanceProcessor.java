package io.hotcloud.service.template;

public interface TemplateInstanceProcessor {

    /**
     * @param template  {@link Template}
     * @param imageUrl  template image e.g. 127.0.0.1/template/minio:latest
     * @param user      current user's name
     * @param namespace current user's namespace
     * @return {@link TemplateInstance}
     */
    TemplateInstance process(Template template, String imageUrl, String user, String namespace);

    boolean support(Template template);
}
