package tech.aiflowy.common.captcha.tainai;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.cache.impl.LocalCacheStore;
import cloud.tianai.captcha.spring.store.impl.RedisCacheStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class CaptchaCacheAutoConfig {

    @Bean
    public CacheStore cacheStore(
            @org.springframework.beans.factory.annotation.Autowired(required = false)
            StringRedisTemplate stringRedisTemplate,
            Environment environment) {

        // 检查是否有Redis配置
        boolean hasRedisConfig = hasRedisConfiguration(environment);

        // 如果有Redis配置且RedisTemplate可用，使用Redis
        if (hasRedisConfig && stringRedisTemplate != null) {
            try {
                // 测试Redis连接
                stringRedisTemplate.getConnectionFactory().getConnection().ping();
                return new RedisCacheStore(stringRedisTemplate);
            } catch (Exception e) {
                // Redis不可用，使用本地缓存
                return new LocalCacheStore();
            }
        }

        // 没有Redis配置，使用本地缓存
        return new LocalCacheStore();
    }

    private boolean hasRedisConfiguration(Environment environment) {
        // 检查常见的Redis配置属性
        String[] redisProperties = {
                "spring.redis.host",
                "spring.redis.port",
                "spring.data.redis.host",
                "spring.data.redis.port"
        };

        for (String property : redisProperties) {
            if (environment.containsProperty(property)) {
                return true;
            }
        }

        return false;
    }
}
