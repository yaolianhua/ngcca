package io.hotcloud.application.api.core;

public interface ApplicationInstanceProcessor <I, O>{

    O process (I input);
}
