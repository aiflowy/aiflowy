package tech.aiflowy.common.constant;

import java.math.BigInteger;

public interface Constants {

    // 超级管理员ID
    BigInteger SUPER_ADMIN_ID = BigInteger.valueOf(1L);
    // 超级管理员角色ID
    BigInteger SUPER_ADMIN_ROLE_ID = BigInteger.valueOf(1L);
    // 默认租户ID
    BigInteger DEFAULT_TENANT_ID = BigInteger.valueOf(1000000L);
    // 登录账户KEY
    String LOGIN_USER_KEY = "loginUser";
    // 超级管理员角色标识
    String SUPER_ADMIN_ROLE_CODE = "super_admin";
    // 租户管理员角色名称
    String TENANT_ADMIN_ROLE_NAME = "租户管理员";
    // 租户管理员角色标识
    String TENANT_ADMIN_ROLE_CODE = "tenant_admin";
    // 创建者字段
    String CREATED_BY = "created_by";
    // 部门ID字段
    String DEPT_ID = "dept_id";
    // 租户ID字段
    String TENANT_ID = "tenant_id";
    // 根部门标识
    String ROOT_DEPT = "root_dept";
}
