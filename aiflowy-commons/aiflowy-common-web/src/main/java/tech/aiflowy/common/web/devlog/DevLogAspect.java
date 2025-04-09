package tech.aiflowy.common.web.devlog;


import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.javassist.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringJoiner;

@Aspect
@Component
@Profile({"dev", "test"})
public class DevLogAspect {

    private static final int maxOutputLengthOfParaValue = 512;
    private static ClassPool classPool = ClassPool.getDefault();

    static {
        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
    }


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || execution(* tech.aiflowy.common.web.controller.BaseCurdController.*(..))")
    public void webLog() {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class<?> controllerClass = signature.getDeclaringType();
        Method method = signature.getMethod();

        String url = request.getRequestURL().toString();
        String param = getRequestParamsString(request);

        int lineNumber = getLineNumber(controllerClass, method);


        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } finally {
            String logInfo = "\n" +
                    "+========================================= Start ==========================================\n" +
                    "| Request        : " + request.getMethod() + " " + url + "\n" +
                    "| Request Params : " + param + "\n" +
                    "| Request IP     : " + request.getRemoteAddr() + "\n" +
                    "| Controller     : " + signature.getDeclaringTypeName() + "." + "(" + controllerClass.getSimpleName() + ".java:" + lineNumber + ")" + "\n" +
                    "| Method         : " + method.getName() + buildParamsString(method) + "\n" +
                    "| Response       : " + getResponseText(result) + "\n" +
                    "| Elapsed Time   : " + (System.currentTimeMillis() - startTime) + " ms" + "\n" +
                    "+========================================== End ===========================================\n";
            System.out.println(logInfo);
        }
        return result;
    }

    private static String getResponseText(Object result) {
        if (result instanceof ModelAndView && ((ModelAndView) result).isReference()) {
            return ((ModelAndView) result).getViewName();
        }

        String originalText;
        if (result instanceof String) {
            originalText = (String) result;
        } else {
            originalText = JSON.toJSONString(result);
        }

        if (StringUtil.noText(originalText)) {
            return "";
        }

        originalText = originalText.replace("\n", "");

        if (originalText.length() > 100) {
            return originalText.substring(0, 100) + "...";
        }

        return originalText;
    }


    private String buildParamsString(Method method) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (Class<?> parameterType : method.getParameterTypes()) {
            joiner.add(parameterType.getSimpleName());
        }
        return joiner.toString();
    }


    private int getLineNumber(Class<?> controllerClass, Method method) throws NotFoundException {
        CtClass ctClass = classPool.get(ClassUtil.getUsefulClass(controllerClass).getName());
        classPool.get(ClassUtil.getUsefulClass(controllerClass).getName());
        String desc = DevLogUtil.getMethodDescWithoutName(method);
        CtMethod ctMethod = ctClass.getMethod(method.getName(), desc);
        return ctMethod.getMethodInfo().getLineNumber(0);
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
                    if (values[0] != null && values[0].length() > maxOutputLengthOfParaValue) {
                        sb.append(values[0], 0, maxOutputLengthOfParaValue).append("...");
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