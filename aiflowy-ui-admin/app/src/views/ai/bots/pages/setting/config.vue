<script setup lang="ts">
import type { AiLlm, BotInfo } from '@aiflowy/types';

import { onMounted, ref, watch } from 'vue';

import { tryit } from '@aiflowy/utils';

import { Plus } from '@element-plus/icons-vue';
import { useDebounceFn } from '@vueuse/core';
import {
  ElCol,
  ElCollapse,
  ElCollapseItem,
  ElIcon,
  ElInputNumber,
  ElMessage,
  ElRow,
  ElSelect,
  ElSlider,
} from 'element-plus';

import { getAiLlmList, updateLlmId, updateLlmOptions } from '#/api';

const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const options = ref<AiLlm[]>([]);
const selectedId = ref<string>('');
const llmConfig = ref({
  maxMessageCount: 0,
  maxReplyLength: 0,
  temperature: 0,
  topK: 0,
  topP: 0,
});

watch(
  props,
  (newValue) => {
    if (!newValue.hasSavePermission) {
      selectedId.value = '没有权限';
    } else if (newValue.bot?.llmId) {
      selectedId.value = newValue.bot.llmId;
    }

    if (newValue.bot) {
      llmConfig.value = newValue.bot.llmOptions;
    }
  },
  { immediate: true },
);

onMounted(async () => {
  const [, res] = await tryit(getAiLlmList({ supportFunctionCalling: true }));

  if (res?.errorCode === 0) {
    options.value = res.data;
  }
});

const handleLlmChange = async (value: string) => {
  if (!props.bot) return;

  const [, res] = await tryit(
    updateLlmId({
      id: props.bot?.id || '',
      llmId: value,
    }),
  );

  if (res?.errorCode === 0) {
    ElMessage.success('保存成功');
  } else {
    ElMessage.error(res?.message || '保存失败');
  }
};
const handleLlmOptionsChange = useDebounceFn(
  (key: keyof typeof llmConfig.value, value: number) => {
    const _value = handleInvalidNumber(value);

    llmConfig.value.temperature = _value;
    updateLlmOptions({
      id: props.bot?.id || '',
      llmOptions: {
        [key]: _value,
      },
    });
  },
  300,
);

/** 处理不合规数值 */
const handleInvalidNumber = (
  value: number,
  min = 0.1,
  max = 1,
  decimal = 1,
) => {
  if (Number.isNaN(value) || value < min) {
    return min;
  } else if (value > max) {
    return max;
  }

  if (value.toString().includes('.')) {
    const factor = 10 ** decimal;
    return Math.floor(value * factor) / factor;
  }

  return value;
};
</script>

<template>
  <div class="flex flex-col gap-3">
    <!-- 大模型 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">大模型</h1>
      <div
        class="flex w-full flex-col justify-between gap-1 rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElSelect
          v-model="selectedId"
          :options="options"
          :props="{ value: 'id', label: 'title' }"
          :disabled="!hasSavePermission"
          @change="handleLlmChange"
        />
        <!-- 温度 -->
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">温度</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopK</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopP</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">最大回复长度</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">携带历史条数</ElCol>
          <ElCol :span="10">
            <ElSlider />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber />
          </ElCol>
        </ElRow>
      </div>
    </div>

    <!-- 技能 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">技能</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="工作流">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>工作流</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="知识库">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>知识库</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="插件">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>插件</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 对话设置 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="问题预设">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>问题预设</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="欢迎语" />
        </ElCollapse>
      </div>
    </div>

    <!-- 发布 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="嵌入" />
          <ElCollapseItem title="API" />
        </ElCollapse>
      </div>
    </div>
  </div>
</template>
