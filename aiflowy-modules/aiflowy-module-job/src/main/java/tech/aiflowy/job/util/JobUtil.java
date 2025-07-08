package tech.aiflowy.job.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.chain.Chain;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.tenant.TenantManager;
import dev.tinyflow.core.Tinyflow;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.service.AiWorkflowService;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.enums.EnumJobType;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.util.SpringContextUtil;
import tech.aiflowy.job.entity.SysJob;
import tech.aiflowy.job.job.JobConstant;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.service.SysAccountService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class JobUtil {

    /**
     * sysJobService.test()
     */
    public static Object execSpringBean(SysJob job) {
        Map<String, Object> jobParams = job.getJobParams();
        if (jobParams != null) {
            String beanMethod = jobParams.get(JobConstant.BEAN_METHOD_KEY).toString();
            String[] strings = StrUtil.subBefore(beanMethod, "(", false).split("\\.");
            Object bean = SpringContextUtil.getBean(strings[0]);
            String param = StrUtil.subBetween(beanMethod, "(", ")");
            try {
                // 调用方法并传递参数
                return invoke(bean, strings[1], getParams(param));
            } catch (Exception e) {
                throw new RuntimeException("执行 beanMethod 报错：", e);
            }
        }
        return null;
    }

    /**
     * tech.aiflowy.job.util.JobUtil.execTest("test",1,0.52D,100L)
     * @param job
     */
    public static Object execJavaClass(SysJob job) {
        Map<String, Object> jobParams = job.getJobParams();
        if (jobParams != null) {
            try {
                String javaMethod = jobParams.get(JobConstant.JAVA_METHOD_KEY).toString();
                String before = StrUtil.subBefore(javaMethod, "(", false);
                String[] strings = before.split("\\.");
                String className = String.join(".", Arrays.copyOf(strings, strings.length - 1));
                String methodName = strings[strings.length - 1];
                String param = StrUtil.subBetween(javaMethod, "(", ")");
                Object obj = Class.forName(className).getDeclaredConstructor().newInstance();;
                return invoke(obj, methodName, getParams(param));
            } catch (Exception e) {
                throw new RuntimeException("执行 javaMethod 报错: ",e);
            }
        }
        return null;
    }

    public static Object execWorkFlow(SysJob job) {
        Map<String, Object> jobParams = job.getJobParams();
        JSONObject obj = new JSONObject(jobParams);
        String workflowId = obj.getString(JobConstant.WORKFLOW_KEY);
        JSONObject params = obj.getJSONObject(JobConstant.WORKFLOW_PARAMS_KEY);
        AiWorkflowService service = SpringContextUtil.getBean(AiWorkflowService.class);

        Object accountId = obj.get(JobConstant.ACCOUNT_ID);
        SysAccountService accountService = SpringContextUtil.getBean(SysAccountService.class);

        try {
            TenantManager.ignoreTenantCondition();

            AiWorkflow workflow = service.getById(workflowId);
            if (workflow != null) {
                Tinyflow tinyflow = workflow.toTinyflow();
                Chain chain = tinyflow.toChain();

                if (accountId != null) {
                    // 设置的归属者
                    SysAccount account = accountService.getById(accountId.toString());
                    if (account != null) {
                        chain.getMemory().put(Constants.LOGIN_USER_KEY,account.toLoginAccount());
                    }
                }

                return chain.executeForResult(params);
            }
        } finally {
            TenantManager.restoreTenantCondition();
        }
        return null;
    }

    public static Object execute(SysJob job) {
        Object res = null;
        Integer jobType = job.getJobType();
        if (EnumJobType.TINY_FLOW.getCode() == jobType) {
            res = execWorkFlow(job);
        }
        if (EnumJobType.SPRING_BEAN.getCode() == jobType) {
            res = execSpringBean(job);
        }
        if (EnumJobType.JAVA_CLASS.getCode() == jobType) {
            res = execJavaClass(job);
        }
        return res;
    }

    public void execTest(String a,Integer b,Double c,Long d) {
        System.out.println("动态执行方法，执行参数：" + "a="+ a + ",b="+ b + ",c="+ c + ",d="+ d);
    }

    private static Object[] getParams(String param) {
        if (StrUtil.isEmpty(param)) {
            return new Object[]{new Class<?>[]{}, new Object[]{}};
        }
        String[] splits = param.split(",");
        Object[] res = new Object[2];
        Object[] params = new Object[splits.length];
        Class<?>[] paramTypes = new Class[splits.length];
        for (int i = 0; i < splits.length; i++) {
            String split = splits[i].trim();
            if (split.startsWith("\"")) {
                params[i] = split.substring(1, split.length() - 1);
                paramTypes[i] = String.class;
            } else if ("true".equals(split) || "false".equals(split)) {
                params[i] = Boolean.valueOf(split);
                paramTypes[i] = Boolean.class;
            } else if (split.endsWith("L")) {
                params[i] = Long.valueOf(split.substring(0, split.length() - 1));
                paramTypes[i] = Long.class;
            } else if (split.endsWith("D")) {
                params[i] = Double.valueOf(split.substring(0, split.length() - 1));
                paramTypes[i] = Double.class;
            } else if (split.endsWith("F")) {
                params[i] = Float.valueOf(split.substring(0, split.length() - 1));
                paramTypes[i] = Float.class;
            } else {
                params[i] = Integer.valueOf(split);
                paramTypes[i] = Integer.class;
            }
        }
        res[0] = paramTypes;
        res[1] = params;
        return res;
    }

    private static Object invoke(Object bean, String methodName, Object[] params) throws Exception {
        Object[] args = (Object[]) params[1];
        if (ArrayUtil.isEmpty(params[1])) {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            return method.invoke(bean);
        } else {
            Method method = bean.getClass().getDeclaredMethod(methodName, (Class<?>[]) params[0]);
            return method.invoke(bean, args);
        }
    }

    public static JobKey getJobKey(SysJob job) {
        return JobKey.jobKey(job.getId().toString(), JobConstant.JOB_GROUP);
    }

    public static TriggerKey getTriggerKey(SysJob job) {
        return TriggerKey.triggerKey(job.getId().toString(), JobConstant.JOB_GROUP);
    }
}
