package tech.aiflowy.auth.config;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.aiflowy.common.annotation.NeedApiKeyAccess;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class NeedApiKeyInterceptor implements HandlerInterceptor {
    @Resource
    private SysApiKeyService sysApiKeyService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if ( handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NeedApiKeyAccess needApiKeyAccess = handlerMethod.getMethodAnnotation(NeedApiKeyAccess.class);
            if (needApiKeyAccess != null) {
                String apiKey = request.getHeader("Authorization");
                if (StringUtil.noText(apiKey)) {
                    throw new BusinessException("请传入apiKey");
                }
                sysApiKeyService.checkApikeyPermission(apiKey, requestURI);

            }
        }
        return true;
    }
}
