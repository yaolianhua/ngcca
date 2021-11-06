package io.hotcloud.core.common;

import java.io.IOException;

@FunctionalInterface
public interface RuntimeCommandLineProcessor<T> {

    T process(Process p) throws IOException;
}
