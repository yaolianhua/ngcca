package io.hotcloud.kubernetes.model.affinity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class NodeSelectorTerm {

    public List<MatchRequirement> matchExpressions = new ArrayList<>();

    public List<MatchRequirement> matchFields = new ArrayList<>();

    @Data
    public static class MatchRequirement {

        private String key;
        private String operator = NodeSelectorOperator.IN;
        private List<String> values = new ArrayList<>();

    }
}
