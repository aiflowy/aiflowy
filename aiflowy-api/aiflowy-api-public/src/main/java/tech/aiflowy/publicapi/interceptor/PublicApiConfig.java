package tech.aiflowy.publicapi.interceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PublicApiConfig implements WebMvcConfigurer {

    @Resource
    private PublicApiInterceptor publicApiInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(publicApiInterceptor)
                .addPathPatterns("/public-api/**")
                .excludePathPatterns("/public-api/bot/chat")
        ;
    }
}
