package io.hotCloud.core.kubernetes.cm;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class ConfigMapCreateParams {

    private Boolean immutable;
    private Map<String, String> data;
    private ConfigMapMetadata metadata;
}
