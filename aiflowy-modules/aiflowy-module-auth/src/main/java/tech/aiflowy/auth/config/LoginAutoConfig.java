package tech.aiflowy.auth.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class LoginAutoConfig implements WebMvcConfigurer {

    @Resource
    private LoginProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SaInterceptor saInterceptor = new SaInterceptor(handle -> {
            StpUtil.checkLogin();
        });
        registry.addInterceptor(saInterceptor)
                .order(100)
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/error")
                .excludePathPatterns("/attachment/**")
                .excludePathPatterns("/api/v1/public/*")
                .excludePathPatterns("/api/v1/account/login")
                .excludePathPatterns("/api/v1/account/register")
                .excludePathPatterns(properties.getExcludesOrEmpty());
    }
}
