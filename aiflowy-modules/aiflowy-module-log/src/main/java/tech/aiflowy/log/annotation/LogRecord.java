package tech.aiflowy.log.annotation;

import java.lang.annotation.*;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LogRecord {

    String value();

    String actionType() default "";


}
