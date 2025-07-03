package tech.aiflowy.common.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UsePermission {
    // 使用哪个模块的权限
    String moduleName();
}
