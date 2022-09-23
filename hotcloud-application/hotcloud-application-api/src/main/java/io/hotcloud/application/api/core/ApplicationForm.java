package io.hotcloud.application.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationForm {

    @Builder.Default
    private String name = "";
    private boolean canHttp;

    @Builder.Default
    private Integer serverPort = 8080;

    @Builder.Default
    private ApplicationInstanceSource source = new ApplicationInstanceSource();

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Map<String,String> envs = new HashMap<>();

}
