package com.zsxfa.cloud.core.aop;

import java.lang.annotation.*;

/**
 * @author zsxfa
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {
    String module() default "";

    String operation() default "";

    String type() default "operation";

    String level() default "0";
}
