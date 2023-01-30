package io.hotcloud.kubernetes.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

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

    public static Resources ofRequest(Map<String, String> requests) {
        Resources resources = new Resources();

        resources.setRequests(requests);
        return resources;
    }

    public static Resources ofLimit(Map<String, String> limits) {
        Resources resources = new Resources();

        resources.setRequests(limits);
        return resources;
    }

}
