package io.hotCloud.core.kubernetes.affinity;

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

        private Operator operator = Operator.In;

        private List<String> values = new ArrayList<>();

    }

    public enum Operator {
        //
        In, NotIn, Exists, DoesNotExist, Gt, Lt
    }
}
