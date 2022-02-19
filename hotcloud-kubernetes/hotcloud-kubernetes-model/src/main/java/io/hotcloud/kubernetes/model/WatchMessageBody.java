package io.hotcloud.kubernetes.model;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class WatchMessageBody {

    private String namespace;
    private String kind;
    private String name;
    private String action;

    public WatchMessageBody(String namespace, String kind, String name, String action) {
        this.namespace = namespace;
        this.kind = kind;
        this.name = name;
        this.action = action;
    }

    public static WatchMessageBody of(String namespace, String kind, String name, String action) {
        return new WatchMessageBody(namespace, kind, name, action);
    }
}
