package io.hotcloud.kubernetes.model.storage;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class ConfigMapVolume {

    private boolean optional;
    private Integer defaultModel;
    private String name;
    private List<Item> items = new ArrayList<>();


    @Data
    public static class Item {
        private String key;
        private String path;
        private Integer mode;
    }
}
