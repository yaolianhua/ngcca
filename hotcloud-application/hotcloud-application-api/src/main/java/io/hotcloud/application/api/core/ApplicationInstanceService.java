package io.hotcloud.application.api.core;

public interface ApplicationInstanceService {

    ApplicationInstance findOne(String user, String name);

    ApplicationInstance saveOrUpdate(ApplicationInstance instance);
}
