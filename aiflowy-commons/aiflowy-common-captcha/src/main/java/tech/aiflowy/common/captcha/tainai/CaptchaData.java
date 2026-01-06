package tech.aiflowy.common.captcha.tainai;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;

public class CaptchaData {

    // 验证码id
    private String  id;
    // 验证码数据
    private ImageCaptchaTrack data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImageCaptchaTrack getData() {
        return data;
    }

    public void setData(ImageCaptchaTrack data) {
        this.data = data;
    }
}
