package tech.aiflowy.common.ai.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FunctionModuleDef {
     String name();
     String title();
     String description();
}
