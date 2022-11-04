package io.hotcloud.application.api.core;

import java.util.List;

public interface ApplicationInstanceService {

    List<ApplicationInstance> find(String user, String name);

    ApplicationInstance findActiveSucceed(String user, String name);
    ApplicationInstance findOne(String id);

    ApplicationInstance saveOrUpdate(ApplicationInstance instance);

    void delete(String id);
}
