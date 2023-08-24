package io.hotcloud.service.buildpack.model;

import io.hotcloud.common.model.JavaRuntime;
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

    public static BuildImage ofSource(String httpGitUrl, String branch, String submodule, String startOptions, String startArgs, JavaRuntime runtime) {
        return BuildImage.builder().source(
                SourceCode.builder()
                        .httpGitUrl(httpGitUrl)
                        .branch(branch)
                        .submodule(submodule)
                        .startOptions(startOptions)
                        .startArgs(startArgs)
                        .runtime(runtime)
                        .build()
        ).build();
    }

    public static BuildImage ofJar(String httpUrl, String startOptions, String startArgs, JavaRuntime runtime) {
        return BuildImage.builder().jar(
                Jar.builder()
                        .packageUrl(httpUrl)
                        .startOptions(startOptions)
                        .startArgs(startArgs)
                        .runtime(runtime)
                        .build()
        ).build();
    }

    public static BuildImage ofWar(String httpUrl, JavaRuntime runtime) {
        return BuildImage.builder().war(
                War.builder()
                        .packageUrl(httpUrl)
                        .runtime(runtime)
                        .build()
        ).build();
    }

    public boolean isSourceCode() {
        return source != null && !isWar() && !isJar();
    }

    public boolean isJar() {
        return jar != null && !isWar() && !isSourceCode();
    }

    public boolean isWar() {
        return war != null && !isJar() && !isSourceCode();
    }



}
