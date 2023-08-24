package io.hotcloud.service.application;

public interface ApplicationInstancePlayer {

    ApplicationInstance play(ApplicationForm form);

    void delete(String id);

    void deleteAll(String user);
}
