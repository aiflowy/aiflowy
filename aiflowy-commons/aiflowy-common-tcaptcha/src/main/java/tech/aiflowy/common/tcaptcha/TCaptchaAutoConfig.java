package tech.aiflowy.common.tcaptcha;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class TCaptchaAutoConfig implements WebMvcConfigurer {

    @Resource
    private TCaptchaValidInterceptor interceptor;

    @Resource
    private TCaptchaConfig tCaptchaConfig;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        String[] validPathPatterns = tCaptchaConfig.getValidPathPatterns();
        Boolean enable = tCaptchaConfig.getEnable();
        if (enable != null && enable && validPathPatterns != null && validPathPatterns.length > 0) {
            registry.addInterceptor(interceptor)
                    .order(1)
                    .addPathPatterns(validPathPatterns);
        }
    }
}
