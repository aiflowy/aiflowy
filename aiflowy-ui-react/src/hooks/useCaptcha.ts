import {useGetManual} from "./useApis";

export const useCaptcha = () => {
    const {loading, doGet} = useGetManual("/api/v1/public/tcaptcha");

    const startCaptcha = (onSuccess: (randstr: string, ticket: string) => void) => {

        doGet().then((resp) => {
            const result = resp.data as any;
            if (result.enable === true && result.appId) {
                // 生成一个验证码对象
                // CaptchaAppId：登录验证码控制台，从【验证管理】页面进行查看。如果未创建过验证，请先新建验证。注意：不可使用客户端类型为小程序的CaptchaAppId，会导致数据统计错误。
                // callback：定义的回调函数
                const callback = (res: any) => {
                    // 第一个参数传入回调结果，结果如下：
                    // ret         Int       验证结果，0：验证成功。2：用户主动关闭验证码。
                    // ticket      String    验证成功的票据，当且仅当 ret = 0 时 ticket 有值。
                    // CaptchaAppId       String    验证码应用ID。
                    // bizState    Any       自定义透传参数。
                    // randstr     String    本次验证的随机串，后续票据校验时需传递该参数。

                    // res（用户主动关闭验证码）= {ret: 2, ticket: null}
                    // res（验证成功） = {ret: 0, ticket: "String", randstr: "String"}
                    // res（请求验证码发生错误，验证码自动返回terror_前缀的容灾票据） = {ret: 0, ticket: "String", randstr: "String",  errorCode: Number, errorMessage: "String"}
                    // 此处代码仅为验证结果的展示示例，真实业务接入，建议基于ticket和errorCode情况做不同的业务处理
                    if (res.ret === 0) {
                        onSuccess(res.randstr, res.ticket);
                    }
                };

                try {
                    // @ts-ignore
                    const captcha = new TencentCaptcha(String(result.appId), callback, {});
                    captcha.show();
                } catch (_err) {
                    console.error(_err)
                    callback({
                        ret: 1,
                        randstr: '@' + Math.random().toString(36).substr(2),
                        errorCode: 1001,
                        errorMessage: 'jsload_error'
                    })
                }
            } else {
                // captcha not enable
                onSuccess("", "")
            }
        })
    }

    return {
        loading,
        startCaptcha
    }
}
