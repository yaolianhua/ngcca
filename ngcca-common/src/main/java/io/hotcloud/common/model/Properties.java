package io.hotcloud.common.model;

import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
@RefreshScope
public @interface Properties {
    String prefix() default "";
}
