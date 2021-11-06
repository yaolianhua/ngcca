package io.hotCloud.core.kubernetes.affinity;

import io.kubernetes.client.openapi.models.V1NodeSelectorRequirement;
import io.kubernetes.client.openapi.models.V1NodeSelectorTerm;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class NodeSelectorTermBuilder {

    private NodeSelectorTermBuilder() {

    }

    public static V1NodeSelectorTerm build(NodeSelectorTerm e) {

        V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();
        List<V1NodeSelectorRequirement> v1NodeSelectorRequirements = e.getMatchExpressions().stream()
                .map(matchRequirement -> {
                    V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                    v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                    v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                    v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                    return v1NodeSelectorRequirement;
                }).collect(Collectors.toList());
        v1NodeSelectorTerm.setMatchExpressions(v1NodeSelectorRequirements);

        List<V1NodeSelectorRequirement> requirements = e.getMatchFields().stream()
                .map(matchRequirement -> {
                    V1NodeSelectorRequirement v1NodeSelectorRequirement = new V1NodeSelectorRequirement();
                    v1NodeSelectorRequirement.setKey(matchRequirement.getKey());
                    v1NodeSelectorRequirement.setOperator(matchRequirement.getOperator().name());
                    v1NodeSelectorRequirement.setValues(matchRequirement.getValues());
                    return v1NodeSelectorRequirement;
                }).collect(Collectors.toList());
        v1NodeSelectorTerm.setMatchFields(requirements);
        return v1NodeSelectorTerm;
    }

    public static List<V1NodeSelectorTerm> build(List<NodeSelectorTerm> nodeSelectorTerms) {
        return nodeSelectorTerms.stream().map(NodeSelectorTermBuilder::build).collect(Collectors.toList());
    }

}
