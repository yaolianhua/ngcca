package io.hotcloud.core.kubernetes.service;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class ServiceReadParams {

    private String namespace;
    private Map<String, String> labelSelector = new HashMap<>();
}
