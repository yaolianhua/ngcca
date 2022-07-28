package io.hotcloud.buildpack.api.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public static BuildImage ofSource(String httpGitUrl, String branch){
        return BuildImage.builder().source(
                        SourceCode.builder()
                                .httpGitUrl(httpGitUrl)
                                .branch(branch)
                                .build()
                ).build();
    }

    public static BuildImage ofJar(String httpUrl, String startOptions, String startArgs){
        return BuildImage.builder().jar(
                Jar.builder()
                        .packageUrl(httpUrl)
                        .startOptions(startOptions)
                        .startArgs(startArgs)
                        .build()
        ).build();
    }

    public static BuildImage ofWar(String httpUrl){
        return BuildImage.builder().war(
                War.builder()
                        .packageUrl(httpUrl)
                        .build()
        ).build();
    }
    @JsonIgnore
    public boolean isSourceCode(){
        return source != null;
    }
    @JsonIgnore
    public boolean isJar(){
        return jar != null;
    }
    @JsonIgnore
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
        private String packageUrl;
    }

}
