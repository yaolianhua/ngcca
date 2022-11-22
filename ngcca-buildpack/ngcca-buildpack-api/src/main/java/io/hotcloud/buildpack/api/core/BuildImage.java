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

    public static BuildImage ofSource(String httpGitUrl, String branch, String submodule, String startOptions, String startArgs) {
        return BuildImage.builder().source(
                SourceCode.builder()
                        .httpGitUrl(httpGitUrl)
                        .branch(branch)
                        .submodule(submodule)
                        .startOptions(startOptions)
                        .startArgs(startArgs)
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
    public boolean isSourceCode(){
        return source != null && !isWar() && !isJar();
    }

    public boolean isJar(){
        return jar != null && !isWar() && !isSourceCode();
    }

    public boolean isWar(){
        return war != null && !isJar() && !isSourceCode();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceCode {

        private String httpGitUrl;
        @Builder.Default
        private String branch = "master";
        private String submodule;
        /**
         * e.g. "-Xms128m -Xmx512m"
         */
        private String startOptions;
        /**
         * e.g. -Dspring.profiles.active=production
         */
        private String startArgs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Jar {
        /**
         * Deploy buildPack from binary jar package
         */
        private String packageUrl;
        /**
         * e.g. "-Xms128m -Xmx512m"
         */
        private String startOptions;
        /**
         * e.g. -Dspring.profiles.active=production
         */
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
