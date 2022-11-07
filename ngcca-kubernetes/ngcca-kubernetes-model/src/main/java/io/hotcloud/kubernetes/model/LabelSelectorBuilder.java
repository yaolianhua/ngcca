package io.hotcloud.kubernetes.model;

import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1LabelSelectorRequirement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class LabelSelectorBuilder {
    private LabelSelectorBuilder() {
    }

    public static V1LabelSelector build(LabelSelector labelSelector) {

        V1LabelSelector v1LabelSelector = new V1LabelSelector();

        List<LabelSelector.LabelSelectorRequirement> matchExpressions = labelSelector.getMatchExpressions();

        List<V1LabelSelectorRequirement> requirements = matchExpressions.stream().map(e -> {
            V1LabelSelectorRequirement v1LabelSelectorRequirement = new V1LabelSelectorRequirement();
            v1LabelSelectorRequirement.setKey(e.getKey());
            v1LabelSelectorRequirement.setValues(e.getValues());
            v1LabelSelectorRequirement.setOperator(e.getOperator().name());
            return v1LabelSelectorRequirement;
        }).collect(Collectors.toList());

        v1LabelSelector.setMatchExpressions(requirements);
        v1LabelSelector.setMatchLabels(labelSelector.getMatchLabels());

        return v1LabelSelector;
    }
}
