<script setup lang="ts">
import { ref } from 'vue';

import { Brush } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

import Chat from '#/components/chat/chat.vue';

const chatRef = ref();

const handleClear = () => {
  chatRef.value.clear();
};
</script>

<template>
  <div
    class="bg-background dark:border-border flex h-full flex-col gap-3 rounded-lg p-3 dark:border"
  >
    <div class="flex justify-between">
      <h1 class="text-base font-medium">
        {{ $t('button.preview') }}
      </h1>
      <ElButton text @click="handleClear">
        <ElIcon class="rotate-180"><Brush /></ElIcon>
      </ElButton>
    </div>
    <div class="relative flex-1">
      <Chat
        ref="chatRef"
        class="absolute inset-0"
        :ishow-chat-conversations="false"
      />
    </div>
  </div>
</template>
