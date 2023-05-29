package io.hotcloud.service.module.env;

import com.google.common.base.CaseFormat;
import io.hotcloud.common.model.FieldIgnore;
import io.hotcloud.common.model.Properties;
import io.hotcloud.service.runner.RunnerProcessor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;


@Component
public class SystemConfiguredEnvironment extends AbstractConfiguredEnvironment implements RunnerProcessor {

    private final ApplicationContext context;

    public SystemConfiguredEnvironment(ApplicationContext context) {
        this.context = context;
    }

    @SneakyThrows
    private static String resolvedPropertyName(String prefix, Field field) {
        field.setAccessible(true);
        if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
            return null;
        }
        if (field.isAnnotationPresent(FieldIgnore.class)) {
            return null;
        }

        if (field.isAnnotationPresent(Properties.class)) {
            Properties properties = field.getAnnotation(Properties.class);
            String p = properties.prefix();
            for (Field innerField : field.getType().getDeclaredFields()) {
                resolvedPropertyName(p, innerField);
            }
        }
        String underscoreFieldReplacedDot = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()).replace("_", ".");
        return String.format("%s.%s", prefix, underscoreFieldReplacedDot);
    }

    @SneakyThrows
    @Override
    protected synchronized void configure() {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
        environment.getSystemProperties().forEach((k, v) -> super.environmentProperties.add(EnvironmentProperty.of(k, String.valueOf(v), true)));

        Map<String, Object> customPropertiesMap = context.getBeansWithAnnotation(Properties.class);
        for (Map.Entry<String, Object> entry : customPropertiesMap.entrySet()) {
            Properties properties = AnnotationUtils.findAnnotation(entry.getValue().getClass(), Properties.class);
            if (Objects.isNull(properties)) {
                continue;
            }
            String prefix = properties.prefix();
            for (Field field : entry.getValue().getClass().getDeclaredFields()) {
                String name = resolvedPropertyName(prefix, field);
                if (!StringUtils.hasText(name)) {
                    continue;
                }
                super.environmentProperties.add(EnvironmentProperty.of(name, field.get(entry.getValue()), false));
            }
        }

    }

    @Override
    public void execute() {
        configure();
    }
}
