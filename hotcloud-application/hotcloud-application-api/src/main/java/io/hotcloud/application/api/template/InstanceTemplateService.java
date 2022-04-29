package io.hotcloud.application.api.template;

import io.hotcloud.application.api.InstanceTemplate;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplateService {

    InstanceTemplate saveOrUpdate(InstanceTemplate instance);

    InstanceTemplate findOne(String id);

    void delete(String id);
}
