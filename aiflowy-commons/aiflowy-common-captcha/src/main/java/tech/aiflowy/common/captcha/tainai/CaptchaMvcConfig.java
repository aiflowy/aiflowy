package tech.aiflowy.common.captcha.tainai;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CaptchaMvcConfig implements WebMvcConfigurer {

    @Resource
    private CaptchaValidInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .order(1)
                .addPathPatterns("/api/v1/auth/login")
                .addPathPatterns("/userCenter/auth/login");
    }
}
