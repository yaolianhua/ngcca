package io.hotcloud.service.ingress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngressDefinition {
    private String namespace;
    private String name;
    @Builder.Default
    private List<Rule> rules = new LinkedList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        private String host;
        private String service;
        private String port;
        @Builder.Default
        private String path = "/";
        @Builder.Default
        private String pathType = "ImplementationSpecific";
    }
}
