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
public class SecretCreateRequest {

    private Boolean immutable;
    private Map<String, String> data = new HashMap<>();
    private String type;
    private ObjectMetadata metadata = new ObjectMetadata();

}
