package io.hotcloud.service.application;

import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;

public interface ApplicationInstancePlayer {

    ApplicationInstance play(ApplicationForm form);

    void delete(String id);

    void deleteAll(String user);
}
