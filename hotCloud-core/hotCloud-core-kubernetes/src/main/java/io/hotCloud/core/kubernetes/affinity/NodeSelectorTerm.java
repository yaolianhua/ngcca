package io.hotCloud.core.kubernetes.affinity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class NodeSelectorTerm {

    @Builder.Default
    public List<MatchRequirement> matchExpressions = new ArrayList<>();
    @Builder.Default
    public List<MatchRequirement> matchFields = new ArrayList<>();

    @Data
    @Builder
    public static class MatchRequirement {

        private String key;
        @Builder.Default
        private Operator operator = Operator.In;
        @Builder.Default
        private List<String> values = new ArrayList<>();

    }

    public enum Operator {
        //
        In, NotIn, Exists, DoesNotExist, Gt, Lt
    }
}
