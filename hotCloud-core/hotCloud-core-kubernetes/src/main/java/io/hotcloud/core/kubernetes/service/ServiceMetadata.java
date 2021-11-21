package io.hotcloud.core.kubernetes.service;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class ServiceMetadata {

    private String namespace = "default";

    @NotBlank(message = "service name is empty")
    private String name;

    private Map<String, String> labels = new HashMap<>();

    private Map<String, String> annotations = new HashMap<>();

}
