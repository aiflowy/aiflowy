<script lang="ts" setup>
import type { AIFlowyFormSchema } from '@aiflowy/common-ui';

import { computed, onMounted } from 'vue';

import { AuthenticationLogin, z } from '@aiflowy/common-ui';
import { useAppConfig } from '@aiflowy/hooks';
import { $t } from '@aiflowy/locales';
import { preferences } from '@aiflowy/preferences';

import { api } from '#/api/request';
import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });
onMounted(() => {});

const authStore = useAuthStore();

const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);

type PlatformType = 'ding_talk' | 'wx_web';

const title = computed(() => preferences.auth.welcomeBack);
const subTitle = computed(() => preferences.auth.loginSubtitle);
const formSchema = computed((): AIFlowyFormSchema[] => {
  return [
    {
      component: 'AIFlowyInput',
      componentProps: {
        placeholder: $t('authentication.usernameTip'),
      },
      fieldName: 'account',
      label: $t('authentication.username'),
      rules: z.string().min(1, { message: $t('authentication.usernameTip') }),
    },
    {
      component: 'AIFlowyInputPassword',
      componentProps: {
        placeholder: $t('authentication.password'),
      },
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, { message: $t('authentication.passwordTip') }),
    },
  ];
});

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
      // 调用具体的login方法
      values.validToken = res.data;
      authStore.authLogin(values);
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

function getAuthUrl(platform: PlatformType) {
  return api.get('/thirdAuth/getAuthUrl', {
    params: {
      platform,
    },
  });
}
</script>

<template>
  <div>
    <AuthenticationLogin
      :form-schema="formSchema"
      :loading="authStore.loginLoading"
      :title="title"
      :sub-title="subTitle"
      @submit="onSubmit"
    />
    <div id="captcha-box" class="captcha-div"></div>
  </div>
</template>

<style scoped>
.captcha-div {
  position: absolute;
  top: 30vh;
  left: 21vh;
}
.platform-icon {
  width: 30px;
  height: 30px;
  cursor: pointer;
}
</style>
