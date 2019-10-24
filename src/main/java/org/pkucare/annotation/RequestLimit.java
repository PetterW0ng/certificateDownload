package org.pkucare.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * 自定义 受限配置 注解
 * Created by weiqin on 2019/10/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {

    /**
     * 允许访问的次数，默认值MAX_VALUE
     */
    int count() default 3;

}
