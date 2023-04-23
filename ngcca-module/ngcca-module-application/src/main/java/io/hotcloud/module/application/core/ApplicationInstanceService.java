package io.hotcloud.module.application.core;

import java.util.List;

public interface ApplicationInstanceService {

    List<ApplicationInstance> find(String user, String name);

    ApplicationInstance findActiveSucceed(String user, String name);

    ApplicationInstance findOne(String id);

    List<ApplicationInstance> findAll();

    List<ApplicationInstance> findAll(String user);

    ApplicationInstance saveOrUpdate(ApplicationInstance instance);

    void delete(String id);
}
