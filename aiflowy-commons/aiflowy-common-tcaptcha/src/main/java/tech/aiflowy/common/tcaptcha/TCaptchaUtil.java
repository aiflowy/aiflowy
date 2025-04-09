package tech.aiflowy.common.tcaptcha;

import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaRceResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaRceResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

public class TCaptchaUtil {
    public static boolean validTicket(String randstr, String ticket) {
        TCaptchaConfig config = TCaptchaConfig.getInstance();
        Credential cred = new Credential(config.getSecretId(), config.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("captcha.tencentcloudapi.com");
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        CaptchaClient client = new CaptchaClient(cred, "", clientProfile);
        DescribeCaptchaRceResultRequest req = new DescribeCaptchaRceResultRequest();
        req.setCaptchaAppId(config.getCaptchaAppId());
        req.setCaptchaType(9L); // 固定值为 9，具体文档查看 https://cloud.tencent.com/document/product/1110/36926
        req.setAppSecretKey(config.getAppSecretKey());
        req.setRandstr(randstr);
        req.setTicket(ticket);
        req.setUserIp("127.0.0.1");
        try {
            DescribeCaptchaRceResultResponse resp = client.DescribeCaptchaRceResult(req);
            // https://cloud.tencent.com/document/product/1110/36926
            return resp != null && resp.getCaptchaCode() == 1;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;
    }

}
