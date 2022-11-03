package io.hotcloud.buildpack.api.core.kaniko;

import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

public class TemplateRender {
    public static final String IMAGEBUILD_SOURCE_TEMPLATE = "imagebuild-source.template";
    public static final String IMAGEBUILD_JAR_WAR_TEMPLATE = "imagebuild-jar-war.template";
    public static final String DOCKERFILE_JAR_TEMPLATE = "Dockerfile-jar.template";
    public static final String DOCKERFILE_JAR_MAVEN_TEMPLATE = "Dockerfile-jar-maven.template";
    public static final String DOCKERFILE_WAR_TEMPLATE = "Dockerfile-war.template";
    public static final String IMAGEBUILD_SECRET_TEMPLATE = "imagebuild-secret.template";

    /**
     * 渲染固定模板  {@code #{[ 此值将被替换 ]}}
     *
     * @param template 给定模板 e.g.
     *                 <pre>{@code
     *                                 FROM #{[ BASE_IMAGE ]}
     *
     *                                 LABEL BUILD_INFO = EDAS_BUILD
     *
     *                                 RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
     *                                 RUN echo 'Asia/Shanghai' > /etc/timezone
     *
     *                                 ENV LANG="en_US.UTF-8"
     *                                 ENV TERM=xterm
     *                                 ENV EDAS_TIMESTAMP currentTime
     *
     *                                 RUN mkdir -p /home/admin/app/
     *                                 RUN wget -q '#{[ PACKAGE_URL ]}' -O /home/admin/app/app.jar
     *                                 RUN echo 'exec java  $CATALINA_OPTS  -jar /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh
     *
     *                                 WORKDIR $ADMIN_HOME
     *
     *                                 CMD ["/bin/bash", "/home/admin/start.sh"]
     *                                 }
     *                                 </pre>
     * @param render   模板参数映射
     */
    public static String apply(String template, Map<String, String> render) {
        if (!StringUtils.hasText(template) || CollectionUtils.isEmpty(render)) {
            return "";
        }

        return new SpelExpressionParser()
                .parseExpression(template, new TemplateParserContext())
                .getValue(render, String.class);

    }

    /**
     * Dockerfile 模板变量名
     */
    interface Dockerfile {
        String JAVA_RUNTIME = "JAVA_RUNTIME";
        String PACKAGE_URL = "PACKAGE_URL";
        String JAR_START_OPTIONS = "JAR_START_OPTIONS";
        String JAR_START_ARGS = "JAR_START_ARGS";
        String MAVEN = "MAVEN";
        String JAR_TARGET_PATH = "JAR_TARGET_PATH";

    }

    /**
     * Kaniko 模板变量名
     */
    interface Kaniko {
        String NAMESPACE = "NAMESPACE";
        String ID = "ID";
        String JOB_NAME = "JOB_NAME";
        String INIT_GIT_CONTAINER_NAME = "INIT_GIT_CONTAINER_NAME";
        String INIT_ALPINE_CONTAINER_NAME = "INIT_ALPINE_CONTAINER_NAME";
        String LABEL_NAME = "LABEL_NAME";
        String SECRET_NAME = "SECRET_NAME";
        String DESTINATION = "DESTINATION";
        String GIT_BRANCH = "GIT_BRANCH";
        String HTTP_GIT_URL = "HTTP_GIT_URL";
        String KANIKO_IMAGE = "KANIKO_IMAGE";
        String KANIKO_CONTAINER_NAME = "KANIKO_CONTAINER_NAME";
        String INIT_GIT_CONTAINER_IMAGE = "INIT_GIT_CONTAINER_IMAGE";
        String INIT_ALPINE_CONTAINER_IMAGE = "INIT_ALPINE_CONTAINER_IMAGE";
        String DOCKERFILE_ENCODED = "DOCKERFILE_ENCODED";
        String DOCKER_CONFIG_JSON = "DOCKER_CONFIG_JSON";

        String HOST_ALIASES = "HOST_ALIASES";
    }

}
