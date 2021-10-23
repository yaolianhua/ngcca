package io.hotCloud.core.common;

@FunctionalInterface
public interface RuntimeCommandLineProcessor<T> {

    T process(Process p);
}
