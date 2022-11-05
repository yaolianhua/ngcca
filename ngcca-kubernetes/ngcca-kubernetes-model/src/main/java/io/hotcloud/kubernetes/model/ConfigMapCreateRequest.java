package io.hotcloud.kubernetes.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class ConfigMapCreateRequest {

    private Boolean immutable;
    private Map<String, String> data = new HashMap<>();
    private Map<String, String> binaryData = new HashMap<>();
    private ObjectMetadata metadata = new ObjectMetadata();
}
