package io.hotcloud.kubernetes.api;

import io.fabric8.kubernetes.client.Watcher;

import java.util.function.BiConsumer;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface WatchCallback<T> extends BiConsumer<Watcher.Action, T> {

}
