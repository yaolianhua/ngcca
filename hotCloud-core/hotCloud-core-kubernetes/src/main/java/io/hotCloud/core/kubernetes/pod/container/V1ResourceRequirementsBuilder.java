package io.hotCloud.core.kubernetes.pod.container;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class V1ResourceRequirementsBuilder {

    private V1ResourceRequirementsBuilder() {
    }

    public static V1ResourceRequirements build(Resources resources) {

        V1ResourceRequirements v1ResourceRequirements = new V1ResourceRequirements();

        Resources.Limits limits = resources.getLimits();
        Map<String, Quantity> limit = new HashMap<>(8);
        if (null != limits) {
            limit.put("cpu", Quantity.fromString(limits.getCpu()));
            limit.put("memory", Quantity.fromString(limits.getMemory()));
        }

        Resources.Requests requests = resources.getRequests();
        Map<String, Quantity> request = new HashMap<>(8);
        if (null != requests) {
            request.put("cpu", Quantity.fromString(requests.getCpu()));
            request.put("memory", Quantity.fromString(requests.getMemory()));
        }

        v1ResourceRequirements.setLimits(limit);
        v1ResourceRequirements.setRequests(request);

        return v1ResourceRequirements;
    }
}
