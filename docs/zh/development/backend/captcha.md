# 腾讯云验证码

![img.png](resource/img.png)

## 前言

腾讯云验证码（Captcha）基于十道安全栅栏， 为网页、App、小程序开发者打造立体、全面的人机验证，最大程度地保护注册登录、活动秒杀、点赞发帖、数据保护等各大场景下的业务安全，同时为您提供更精细化的用户体验。
官方网站：https://cloud.tencent.com/document/product/1110

注意：

腾讯云验证码是收费的，但是价格极低，而且体验极好，在一般的个人站点中，充值 100 用 1 年问题不大。

## 如何使用？

在 AIFlowy 中，已经内置了 腾讯云验证码 的对接，可以通过在 application.yml添加如下的配置轻松使用。

```yml
AIFlowy:
  tcaptcha:
    enable:
    secret-id:
    secret-key:
    app-secret-key:
    captcha-app-id:
    valid-path-patterns:
```

- **enable**: 是否启用，`boolean` 类型。
- **secret-id**: 秘钥 ID，通过 [https://console.cloud.tencent.com/cam/capi](https://console.cloud.tencent.com/cam/capi) 可以创建或查看。
- **secret-key**: 秘钥 KEY，通过 [https://console.cloud.tencent.com/cam/capi](https://console.cloud.tencent.com/cam/capi) 可以创建或查看。
- **captcha-app-id**: 验证码 app id，在 [https://console.cloud.tencent.com/captcha/graphical](https://console.cloud.tencent.com/captcha/graphical) 可以创建或查看。
- **app-secret-key**: 验证码 app 的秘钥 key，在 [https://console.cloud.tencent.com/captcha/graphical](https://console.cloud.tencent.com/captcha/graphical) 可以创建或查看。
- **valid-path-patterns**: 验证路径，哪些路径需要通过腾讯验证码进行验证。

## 登录验证

```yml
AIFlowy:
  tcaptcha:
    enable: true
    secret-id: AKID894sT****
    secret-key: e4KUULw****
    captcha-app-id: 19308****
    app-secret-key: 6IwI6o***
    valid-path-patterns: /api/v1/account/login
```
在 valid-path-patterns中，添加对 /api/v1/account/login路径的验证功能。当开启如上配置后，前端登录自动进行验证码验证。

## 自定义验证

在 APP 中，除了登录功能以外，如果我们还需要验证码进行验证，这需要这两个步骤：
1、后台添加对该路径的拦截。
2、前台使用 useCaptchaHook 调用拦截。

## 后台添加拦截

后台拦截配置主要是通过 application.yml添加 AIFlowy.tcaptcha.valid=path-patterns的配置，如下所示：

```yml
AIFlowy:
  tcaptcha:
    enable: true
    secret-id: AKID894sT****
    secret-key: e4KUULw****
    captcha-app-id: 19308****
    app-secret-key: 6IwI6o***
    valid-path-patterns: /api/v1/account/login, 要拦截的 URL
```

## 前台使用  useCaptchaHook 拦截

```tsx
const MyComponent: React.FC = () => {

    // 使用 useCaptcha hook
    const {startCaptcha} = useCaptcha();
 
    const startDoSomeThing = ()=>{
      // 开始弹出验证码并让用户进行验证
      startCaptcha((randstr, ticket) => {
              //验证成功，把 randstr，ticket 传给后台
              doSomeThing({
                  data: {
                      randstr,
                      ticket,
                      ...values, //其他的业务数据
                  }
              }).then((resp) => {
                  //....
              }).catch((e) => {
                  message.error("网络错误：" + e)
              })
          })
    }

    return (
         <Button onClick={()=>{startDoSomeThing()}}>
            登 录
        </Button>
    )
}
export default MyComponent;
```
