package org.pkucare.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weiqin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelExport {
    /**
     * 列名
     * @return
     */
    String value() default "";

    String dataFormate() default "";

}
