package io.hotcloud.kubernetes.model;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class WatchMessageBody {

    private String namespace;
    private String name;
    private String action;

    public WatchMessageBody(String namespace, String name, String action) {
        this.namespace = namespace;
        this.name = name;
        this.action = action;
    }

    public static WatchMessageBody of(String namespace, String name, String action) {
        return new WatchMessageBody(namespace, name, action);
    }
}
