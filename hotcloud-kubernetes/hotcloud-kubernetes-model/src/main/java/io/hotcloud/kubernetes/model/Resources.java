package io.hotcloud.kubernetes.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Resources {

    private Map<String, String> limits = new HashMap<>();
    @NotEmpty(message = "resources[storage]: Required value")
    private Map<String, String> requests = new HashMap<>();

}
