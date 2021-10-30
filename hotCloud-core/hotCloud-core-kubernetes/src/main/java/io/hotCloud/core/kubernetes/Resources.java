package io.hotCloud.core.kubernetes;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Resources {

    private Map<String, String> limits = new HashMap<>();
    private Map<String, String> requests = new HashMap<>();

}
