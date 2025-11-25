<script setup lang="ts">
import type { Recordable } from '@aiflowy/types';

import type { AIFlowyFormSchema } from '@aiflowy-core/form-ui';

import { computed, reactive } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { useAIFlowyForm } from '@aiflowy-core/form-ui';
import { AIFlowyButton } from '@aiflowy-core/shadcn-ui';

import Title from './auth-title.vue';

interface Props {
  formSchema?: AIFlowyFormSchema[];
  /**
   * @zh_CN 是否处于加载处理状态
   */
  loading?: boolean;
  /**
   * @zh_CN 登录路径
   */
  loginPath?: string;
  /**
   * @zh_CN 标题
   */
  title?: string;
  /**
   * @zh_CN 描述
   */
  subTitle?: string;
  /**
   * @zh_CN 按钮文本
   */
  submitButtonText?: string;
}

defineOptions({
  name: 'RegisterForm',
});

const props = withDefaults(defineProps<Props>(), {
  formSchema: () => [],
  loading: false,
  loginPath: '/auth/login',
  submitButtonText: '',
  subTitle: '',
  title: '',
});

const emit = defineEmits<{
  submit: [Recordable<any>];
}>();

const [Form, formApi] = useAIFlowyForm(
  reactive({
    commonConfig: {
      hideLabel: true,
      hideRequiredMark: true,
    },
    schema: computed(() => props.formSchema),
    showDefaultActions: false,
  }),
);

const router = useRouter();

async function handleSubmit() {
  const { valid } = await formApi.validate();
  const values = await formApi.getValues();
  if (valid) {
    emit('submit', values as { password: string; username: string });
  }
}

function goToLogin() {
  router.push(props.loginPath);
}

defineExpose({
  getFormApi: () => formApi,
});
</script>

<template>
  <div>
    <Title>
      <slot name="title">
        {{ title || $t('authentication.createAnAccount') }} 🚀
      </slot>
      <template #desc>
        <slot name="subTitle">
          {{ subTitle || $t('authentication.signUpSubtitle') }}
        </slot>
      </template>
    </Title>
    <Form />

    <AIFlowyButton
      :class="{
        'cursor-wait': loading,
      }"
      :loading="loading"
      aria-label="register"
      class="mt-2 w-full"
      @click="handleSubmit"
    >
      <slot name="submitButtonText">
        {{ submitButtonText || $t('authentication.signUp') }}
      </slot>
    </AIFlowyButton>
    <div class="mt-4 text-center text-sm">
      {{ $t('authentication.alreadyHaveAccount') }}
      <span class="aiflowy-link text-sm font-normal" @click="goToLogin()">
        {{ $t('authentication.goToLogin') }}
      </span>
    </div>
  </div>
</template>
