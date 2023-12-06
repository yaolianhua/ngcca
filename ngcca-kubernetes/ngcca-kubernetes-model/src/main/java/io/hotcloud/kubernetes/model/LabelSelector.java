package io.hotcloud.kubernetes.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class LabelSelector {

    public LabelSelector() {
    }

    public LabelSelector(Map<String, String> matchLabels, List<LabelSelectorRequirement> matchExpressions) {
        this.matchLabels = matchLabels;
        this.matchExpressions = matchExpressions;
    }

    @NotEmpty(message = "matchLabels is empty")
    private Map<String, String> matchLabels = new HashMap<>();

    private List<LabelSelectorRequirement> matchExpressions = new ArrayList<>();

    @Data
    public static class LabelSelectorRequirement{
        private String key;

        private String operator = LabelSelectorOperator.IN;

        private List<String> values = new ArrayList<>();
    }
}
