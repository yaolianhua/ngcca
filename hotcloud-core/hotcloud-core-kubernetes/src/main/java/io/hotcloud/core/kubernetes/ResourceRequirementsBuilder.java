package io.hotcloud.core.kubernetes;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ResourceRequirementsBuilder {

    private ResourceRequirementsBuilder() {
    }

    public static V1ResourceRequirements build(Resources resources) {

        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();

        Map<String, String> limits = resources.getLimits();
        Map<String, Quantity> limit = new HashMap<>(8);
        if (null != limits) {
            limits.forEach((key, value) -> limit.put(key, Quantity.fromString(value)));
        }

        Map<String, String> requests = resources.getRequests();
        Map<String, Quantity> request = new HashMap<>(8);
        if (null != requests) {
            requests.forEach((key, value) -> request.put(key, Quantity.fromString(value)));
        }

        v1ResourceRequirements.setLimits(limit);
        v1ResourceRequirements.setRequests(request);

        return v1ResourceRequirements;
    }
}
