package tech.aiflowy.common.captcha.tainai;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.CrudResourceStore;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CaptchaConfig {

    private final ResourceStore resourceStore;

    public CaptchaConfig(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    @PostConstruct
    public void init() {

        CrudResourceStore resourceStore = (CrudResourceStore) this.resourceStore;
        // 添加自定义背景图片
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/1.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/2.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/3.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/4.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/5.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/6.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/7.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/8.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/9.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha-images/10.jpg", "default"));
    }
}
