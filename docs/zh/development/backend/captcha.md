# 行为验证码
行为验证码框架为：[tianai-captcha](https://gitee.com/dromara/tianai-captcha)

## 验证逻辑
### 后端
`tech.aiflowy.common.captcha.tainai.CaptchaMvcConfig`
类配置了需要验证的接口，通过获取请求体中的 `validToken` 字段来判断行为验证码是否验证完成。

行为验证和业务代码是解耦的设计，需要用验证码来限制的接口都可以在这里配置，拦截器会自动进行验证，业务代码无需做任何处理。

### 前端
如需对某个操作进行验证码验证，可参考

`aiflowy-ui-admin > app > src > views > _core > authentication > login.vue`

中的代码。

```js
function onSubmit(values: any) {
  // config 对象为TAC验证码的一些配置和验证的回调
  const config = {
    // 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
    requestCaptchaDataUrl: `${apiURL}/api/v1/public/getCaptcha`,
    // 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
    validCaptchaUrl: `${apiURL}/api/v1/public/check`,
    // 验证码绑定的div块 (必选项,必须配置)
    bindEl: '#captcha-box',
    // 验证成功回调函数(必选项,必须配置)
    validSuccess: (res: any, _: any, tac: any) => {
      // 销毁验证码服务
      tac.destroyWindow();
      // ！！！在这里调用业务接口！！！
    },
    // 验证失败的回调函数(可忽略，如果不自定义 validFail 方法时，会使用默认的)
    validFail: (_: any, __: any, tac: any) => {
      // 验证失败后重新拉取验证码
      tac.reloadCaptcha();
    },
    // 刷新按钮回调事件
    btnRefreshFun: (_: any, tac: any) => {
      tac.reloadCaptcha();
    },
    // 关闭按钮回调事件
    btnCloseFun: (_: any, tac: any) => {
      tac.destroyWindow();
    },
  };
  const style = {
    logoUrl: null, // 去除logo
    // logoUrl: "/xx/xx/xxx.png" // 替换成自定义的logo
    btnUrl: '/tac-btn.png',
  };
  window
    // @ts-ignore
    .initTAC('/tac', config, style)
    .then((tac: any) => {
      tac.init(); // 调用init则显示验证码
    })
    .catch((error: any) => {
      console.error('初始化tac失败', error);
    });
}
```
## 自定义图片

AIFlowy 内置了十张验证码图片，你可以自行替换或者增加。

在 `tech.aiflowy.common.captcha.tainai.CaptchaConfig` 类中配置。

然后在 `starter` 的 `resources` 目录下添加你的图片，注意名字要和配置文件内对应，图片宽高为：600x360