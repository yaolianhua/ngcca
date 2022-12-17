package io.hotcloud.application.api.core;

public interface ApplicationInstancePlayer {

    ApplicationInstance play (ApplicationForm form);

    void delete(String id);

    void deleteAll(String user);
}
