package io.hotcloud.application.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCreate {

    @Builder.Default
    private String name = "";

    @Builder.Default
    private Integer serverPort = 8080;

    @Builder.Default
    private ApplicationInstanceSource source = new ApplicationInstanceSource();

    @Builder.Default
    private Integer replicas = 1;
    private Map<String,String> envs;

}
