package io.hotcloud.module.application.core;

public interface ApplicationInstancePlayer {

    ApplicationInstance play(ApplicationForm form);

    void delete(String id);

    void deleteAll(String user);
}
