<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';
import { tryit } from '@aiflowy/utils';

import { ArrowLeft, Check } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDescriptions,
  ElDescriptionsItem,
  ElLink,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import { router } from '#/router';

const feedbackTypeOptions = [
  {
    value: 1,
    label: $t('sysFeedback.functionalFailure'),
  },
  {
    value: 2,
    label: $t('sysFeedback.optimizationSuggestions'),
  },
  {
    value: 3,
    label: $t('sysFeedback.accountIssue'),
  },
  {
    value: 4,
    label: $t('sysFeedback.other'),
  },
];
const statusType: Record<number, any> = {
  0: {
    label: $t('sysFeedback.notViewed'),
    type: 'warning',
  },
  1: {
    label: $t('sysFeedback.viewed'),
    type: 'primary',
  },
  2: {
    label: $t('sysFeedback.processed'),
    type: 'success',
  },
  3: {
    label: $t('sysFeedback.closed/Invalid'),
    type: 'info',
  },
};

const route = useRoute();
const feedback = ref();
const status = ref(0);
const loading = reactive<Record<string, boolean>>({
  markRead: false,
  markResolved: false,
});

onMounted(async () => {
  if (route.params.id) {
    const [, res] = await tryit(
      api.get('/api/v1/sysUserFeedback/detail', {
        params: { id: route.params.id },
      }),
    );

    if (res && res.errorCode === 0) {
      feedback.value = res.data;
      status.value = res.data.status;
    }
  }
});

function getFeedbackType(type: number) {
  const option = feedbackTypeOptions.find((option) => option.value === type);
  return option?.label;
}
async function markStatus(_status: number) {
  const key = _status === 1 ? 'markRead' : 'markResolved';
  loading[key] = true;

  const [, res] = await tryit(
    api.post('/api/v1/sysUserFeedback/update', {
      ...feedback.value,
      status: _status,
    }),
  );

  if (res && res.errorCode === 0) {
    status.value = _status;
    ElMessage.success($t('sysFeedback.markedSuccessfully'));
  }
  loading[key] = false;
}
</script>

<template>
  <div class="relative p-5">
    <ElButton
      class="absolute left-5 top-5"
      :icon="ArrowLeft"
      @click="router.back()"
    >
      {{ $t('button.back') }}
    </ElButton>

    <div class="mx-auto flex w-full max-w-[900px] flex-col gap-4">
      <div
        class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
      >
        <ElDescriptions :title="$t('sysFeedback.basicInformation')">
          <ElDescriptionsItem :label="$t('sysFeedback.category')">
            {{ getFeedbackType(feedback?.feedbackType) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem :label="$t('sysFeedback.processingStatus')">
            {{ statusType[feedback?.status ?? 0].label }}
          </ElDescriptionsItem>
          <ElDescriptionsItem :label="$t('sysFeedback.contactInformation')">
            {{ feedback?.contactInfo }}
          </ElDescriptionsItem>
          <ElDescriptionsItem :label="$t('sysFeedback.submittedAt')">
            {{ feedback?.created }}
          </ElDescriptionsItem>
        </ElDescriptions>
        <!-- <h1 class="text-base font-medium">基础信息</h1>
        <div class="flex items-center gap-16 text-sm">
          <span>问题类型：{{ feedback?.feedbackType }}</span>
          <span>处理状态：{{ feedback?.status }}</span>
          <span>联系方式：{{ feedback?.contactInfo }}</span>
          <span>提交时间：{{ feedback?.created }}</span>
        </div> -->
      </div>

      <div
        class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
      >
        <h1 class="text-base font-medium">
          {{ $t('sysFeedback.feedbackContent') }}
        </h1>
        <div class="flex items-start gap-2 text-sm">
          <span class="w-20 shrink-0">
            {{ $t('sysFeedback.description') }}：
          </span>
          <div
            class="border-border w-full whitespace-pre-wrap rounded-lg border p-4"
          >
            {{ feedback?.feedbackContent }}
          </div>
        </div>
        <div class="flex items-start gap-2 text-sm">
          <span class="w-20 shrink-0">
            {{ $t('sysFeedback.attachments') }}：
          </span>
          <ElLink target="_blank" :href="feedback?.attachmentUrl">
            {{ feedback?.attachmentUrl }}
          </ElLink>
        </div>
      </div>

      <div class="mx-auto flex items-center">
        <ElButton
          type="primary"
          :icon="Check"
          :loading="loading.markRead"
          :disabled="status >= 1"
          @click="markStatus(1)"
        >
          {{ $t('button.markAsRead') }}
        </ElButton>
        <ElButton
          type="primary"
          :loading="loading.markResolved"
          :disabled="status >= 2"
          @click="markStatus(2)"
        >
          <template #icon>
            <IconifyIcon icon="svg:resolved" />
          </template>
          {{ $t('button.markAsResolved') }}
        </ElButton>
      </div>
    </div>
  </div>
</template>
