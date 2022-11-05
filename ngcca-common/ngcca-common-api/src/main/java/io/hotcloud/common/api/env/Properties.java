package io.hotcloud.common.api.env;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface Properties {
    String prefix() default "";
}
