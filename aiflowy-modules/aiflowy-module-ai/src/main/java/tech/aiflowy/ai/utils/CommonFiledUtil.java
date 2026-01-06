package tech.aiflowy.ai.utils;

import tech.aiflowy.common.web.exceptions.ProgramException;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Date;

public class CommonFiledUtil {
    public static void commonFiled(Object t, BigInteger userId, BigInteger tenantId, BigInteger deptId) {
        Method[] methods = t.getClass().getMethods();
        try {
            for (Method m : methods) {
                String name = m.getName();
                if ("setDeptId".equals(name)) {
                    m.invoke(t, deptId);
                }
                if ("setTenantId".equals(name)) {
                    m.invoke(t, tenantId);
                }
                if ("setCreatedBy".equals(name)) {
                    m.invoke(t, userId);
                }
                if ("setModifiedBy".equals(name)) {
                    m.invoke(t, userId);
                }
                if ("setCreated".equals(name)) {
                    m.invoke(t, new Date());
                }
                if ("setModified".equals(name)) {
                    m.invoke(t, new Date());
                }
            }
        } catch (Exception e) {
            throw new ProgramException("commonFiled反射出错：" + e.getMessage());
        }
    }
}
