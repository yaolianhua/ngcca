package io.hotcloud.web.mvc;

import io.hotcloud.common.model.activity.Action;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Log {
    Action action();

    io.hotcloud.common.model.activity.Target target();

    String activity() default "";
}
