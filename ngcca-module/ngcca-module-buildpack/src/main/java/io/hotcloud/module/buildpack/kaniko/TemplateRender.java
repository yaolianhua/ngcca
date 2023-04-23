package io.hotcloud.module.buildpack.kaniko;

import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

public class TemplateRender {

    /**
     * 渲染固定模板  {@code #{[ 此值将被替换 ]}}
     *
     * @param template 给定模板 e.g.
     *                 <pre>{@code
     *                                                                 FROM #{[ BASE_IMAGE ]}
     *
     *                                                                 LABEL BUILD_INFO = EDAS_BUILD
     *
     *                                                                 RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
     *                                                                 RUN echo 'Asia/Shanghai' > /etc/timezone
     *
     *                                                                 ENV LANG="en_US.UTF-8"
     *                                                                 ENV TERM=xterm
     *                                                                 ENV EDAS_TIMESTAMP currentTime
     *
     *                                                                 RUN mkdir -p /home/admin/app/
     *                                                                 RUN wget -q '#{[ PACKAGE_URL ]}' -O /home/admin/app/app.jar
     *                                                                 RUN echo 'exec java  $CATALINA_OPTS  -jar /home/admin/app/app.jar' > /home/admin/start.sh && chmod +x /home/admin/start.sh
     *
     *                                                                 WORKDIR $ADMIN_HOME
     *
     *                                                                 CMD ["/bin/bash", "/home/admin/start.sh"]
     *                                                                 }
     *                                                                 </pre>
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

}
