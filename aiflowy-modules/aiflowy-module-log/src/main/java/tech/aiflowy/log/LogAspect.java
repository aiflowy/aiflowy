package tech.aiflowy.log;

import tech.aiflowy.common.util.RequestUtil;
import tech.aiflowy.common.util.StringUtil;

import tech.aiflowy.log.annotation.LogRecord;
import tech.aiflowy.log.entity.WriteLog;
import tech.aiflowy.log.mapper.WriteLogMapper;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import cn.dev33.satoken.stp.StpUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;


@Aspect
@Component
public class LogAspect {

    private static final int maxLengthOfParaValue = 512;

    private final WriteLogMapper logService;
    private final LogRecordProperties config;

    public LogAspect(WriteLogMapper logService, LogRecordProperties config) {
        this.logService = logService;
        this.config = config;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) " +
            "|| execution(* tech.aiflowy.common.web.controller.BaseCurdController.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String servletPath = request.getServletPath();

        //匹配前缀
        if (StringUtil.hasText(config.getRecordActionPrefix()) && !servletPath.startsWith(config.getRecordActionPrefix())) {
            return proceedingJoinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class<?> controllerClass = signature.getDeclaringType();
        Method method = signature.getMethod();
        String params = getRequestParamsString(request);

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            WriteLog sysLog = new WriteLog();
            LogRecord logRecord = method.getAnnotation(LogRecord.class);
            if (StpUtil.isLogin()) {

                BigInteger accountId = SaTokenUtil.getLoginAccount().getId();
                sysLog.setAccountId(accountId);
            }
            sysLog.setActionName(buildActionName(logRecord, method));
            sysLog.setActionType(logRecord != null ? logRecord.actionType() : null);
            sysLog.setActionClass(controllerClass.getName());
            sysLog.setActionMethod(method.getName());
            sysLog.setActionUrl(request.getRequestURL().toString());
            sysLog.setActionIp(RequestUtil.getIpAddress(request));
            sysLog.setActionParams(params);
            sysLog.setStatus(1);
            sysLog.setCreated(new Date());

            logService.insert(sysLog);
        }
    }

    private String buildActionName(LogRecord logRecord, Method method) {
        if (logRecord != null && StringUtil.hasText(logRecord.value())) {
            return logRecord.value();
        } else {
            //todo 这里可以通过方法名，去获取 Controller 的实体类，在获取其表备注信息，进一步进行判断
            return method.getName();
        }
    }

    private String getRequestParamsString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> e = request.getParameterNames();
        if (e.hasMoreElements()) {
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    sb.append(name).append("=");
                    if (values[0] != null && values[0].length() > maxLengthOfParaValue) {
                        sb.append(values[0], 0, maxLengthOfParaValue).append("...");
                    } else {
                        sb.append(values[0]);
                    }
                } else {
                    sb.append(name).append("[]={");
                    for (int i = 0; i < values.length; i++) {
                        if (i > 0) {
                            sb.append(",");
                        }
                        sb.append(values[i]);
                    }
                    sb.append("}");
                }
                sb.append("  ");
            }
        }
        return sb.toString();
    }


}
