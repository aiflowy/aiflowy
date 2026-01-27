<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { ref, watch } from 'vue';

import { useDebounceFn } from '@vueuse/core';
import { ElIcon, ElInput } from 'element-plus';

import { updateLlmOptions } from '#/api';
import MagicStaffIcon from '#/components/icons/MagicStaffIcon.vue';
import { $t } from '#/locales';
import PromptChoreChatModal from '#/views/ai/bots/pages/setting/PromptChoreChatModal.vue';

const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const systemPrompt = ref($t('bot.placeholder.prompt'));
const promptChoreChatModalRef = ref();
watch(
  () => props.bot?.modelOptions.systemPrompt,
  (newPrompt) => {
    if (newPrompt) {
      systemPrompt.value = newPrompt;
    }
  },
  { immediate: true },
);

const handleInput = useDebounceFn((value: string) => {
  updateLlmOptions({
    id: props.bot?.id || '',
    llmOptions: {
      systemPrompt: value,
    },
  });
}, 1000);

const handelReplacePrompt = (value: string) => {
  systemPrompt.value = value;
  handleInput(value);
};
</script>

<template>
  <div
    class="bg-background dark:border-border flex h-full flex-col gap-2 rounded-lg p-3 dark:border"
  >
    <div class="flex justify-between">
      <h1 class="text-base font-medium">
        {{ $t('bot.systemPrompt') }}
      </h1>
      <button
        @click="promptChoreChatModalRef.open(props.bot?.id, systemPrompt)"
        type="button"
        class="flex items-center gap-0.5 rounded-lg bg-[#f7f7f7] px-3 py-1"
      >
        <ElIcon size="16"><MagicStaffIcon /></ElIcon>
        <span
          class="bg-[linear-gradient(106.75666073298856deg,#F17E47,#D85ABF,#717AFF)] bg-clip-text text-sm text-transparent"
        >
          {{ $t('bot.aiOptimization') }}
        </span>
      </button>
    </div>
    <ElInput
      class="flex-1"
      type="textarea"
      resize="none"
      v-model="systemPrompt"
      :title="!hasSavePermission ? $t('bot.placeholder.permission') : ''"
      :disabled="!hasSavePermission"
      @input="handleInput"
    />

    <!--系统提示词优化模态框-->
    <PromptChoreChatModal
      ref="promptChoreChatModalRef"
      @success="handelReplacePrompt"
    />
  </div>
</template>

<style lang="css" scoped>
.el-textarea :deep(.el-textarea__inner) {
  --el-input-bg-color: #f7f7f7;

  height: 100%;
  padding: 12px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.25;
  border-radius: 8px;
  box-shadow: none;
}

.dark .el-textarea :deep(.el-textarea__inner) {
  --el-input-bg-color: hsl(var(--background-deep));

  border: 1px solid hsl(var(--border));
}
</style>
