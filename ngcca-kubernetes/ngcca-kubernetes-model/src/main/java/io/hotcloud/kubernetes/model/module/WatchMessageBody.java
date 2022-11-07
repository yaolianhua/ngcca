package io.hotcloud.kubernetes.model.module;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class WatchMessageBody implements Serializable {

    private String namespace;
    private String kind;
    private String name;
    private String action;

    private Map<String, String> labels = new HashMap<>();

    public WatchMessageBody(String namespace, String kind, String name, String action) {
        this.namespace = namespace;
        this.kind = kind;
        this.name = name;
        this.action = action;
    }

    public static WatchMessageBody of(String namespace, String kind, String name, String action) {
        return new WatchMessageBody(namespace, kind, name, action);
    }

    public static WatchMessageBody of(String namespace, String kind, String name, String action, Map<String, String> labels) {
        WatchMessageBody watchMessageBody = new WatchMessageBody(namespace, kind, name, action);
        watchMessageBody.setLabels(labels);
        return watchMessageBody;
    }
}
