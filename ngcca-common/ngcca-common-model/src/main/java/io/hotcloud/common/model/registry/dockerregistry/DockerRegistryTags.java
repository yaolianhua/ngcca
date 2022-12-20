package io.hotcloud.common.model.registry.dockerregistry;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DockerRegistryTags {

    private String name;
    private List<String> tags = new LinkedList<>();
}
