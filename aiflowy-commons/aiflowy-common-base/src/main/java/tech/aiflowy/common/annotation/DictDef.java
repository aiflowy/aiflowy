package tech.aiflowy.common.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DictDef {
    String name();
    String code();
    String keyField();
    String labelField();
}
