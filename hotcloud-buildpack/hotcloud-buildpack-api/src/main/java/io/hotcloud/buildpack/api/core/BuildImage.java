package io.hotcloud.buildpack.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildImage {

    private SourceCode source;
    private Jar jar;
    private War war;

    public static BuildImage of(String httpGitUrl, String branch){
        return BuildImage.builder().source(
                        SourceCode.builder()
                                .httpGitUrl(httpGitUrl)
                                .branch(branch)
                                .build()
                ).build();
    }
    public boolean isSourceCode(){
        return source != null;
    }

    public boolean isJar(){
        return jar != null;
    }

    public boolean isWar(){
        return war != null;
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceCode{

        private String httpGitUrl;
        private String branch;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Jar{
        private String packageUrl;
        private String startOptions;
        private String startArgs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class War{

    }

}
