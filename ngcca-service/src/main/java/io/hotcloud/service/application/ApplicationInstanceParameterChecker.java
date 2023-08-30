package io.hotcloud.service.application;

import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;

public interface ApplicationInstanceParameterChecker {

    ApplicationInstance check(ApplicationForm form);
}
