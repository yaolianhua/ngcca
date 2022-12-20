package io.hotcloud.common.model.registry.dockerregistry;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DockerRegistryCatalog {

    private List<String> repositories = new LinkedList<>();
}
