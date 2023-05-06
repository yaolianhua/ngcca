package io.hotcloud.module.application.core;

import io.hotcloud.common.model.JavaRuntime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInstanceSource {

    private Origin origin;

    @Builder.Default
    private JavaRuntime runtime = JavaRuntime.JAVA17;
    /**
     * <ul>
     *   <li/> <a href="http://127.0.0.1:9000/minio-public-buket/demo.jar">http://127.0.0.1:9000/minio-public-buket/demo.jar</a>
     *   <li/> <a href="http://127.0.0.1:9000/minio-public-buket/demo.war">http://127.0.0.1:9000/minio-public-buket/demo.war</a>
     *   <li/> <a href="http://127.0.0.1:5000/namespace/demo:v1">http://127.0.0.1:5000/namespace/demo:v1</a>
     *   <li/> <a href="https://gitee.com/yannanshan/devops-thymeleaf.git">https://gitee.com/yannanshan/devops-thymeleaf.git</a>
     * <ul/>
     */
    private String url;
    /**
     * if origin value is {@code source_code}. the value is not null, default {@code master}
     */
    private String gitBranch;

    /**
     * if origin value is {@code source_code}. the value can be set, default {@code null}
     */
    private String gitSubmodule;
    /**
     * e.g. -Dspring.profiles.active=production
     */
    private String startArgs;
    /**
     * e.g. "-Xms128m -Xmx512m"
     */
    private String startOptions;

    public enum Origin {
        //
        WAR,
        //
        JAR,
        //
        SOURCE_CODE,
        //
        IMAGE
    }
}
