<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { onBeforeMount, ref } from 'vue';
import { useRoute } from 'vue-router';

import { ChatDotRound } from '@element-plus/icons-vue';
import {
  ElAside,
  ElAvatar,
  ElButton,
  ElContainer,
  ElMain,
  ElMessage,
  ElSpace,
  ElText,
} from 'element-plus';

import { getBotDetails } from '#/api';
import { $t } from '#/locales';

const route = useRoute();
const bot = ref<BotInfo>();

onBeforeMount(async () => {
  if (route.params.id) {
    try {
      const result = await getBotDetails(route.params.id as string);

      if (result.errorCode === 0) {
        bot.value = result.data;
      } else {
        ElMessage.error(result.message);
      }
    } catch (error) {
      console.error(error);
    }
  }
});
</script>

<template>
  <ElContainer class="h-screen" v-if="bot">
    <ElAside width="200px" class="flex flex-col items-center bg-[#f5f5f580]">
      <ElSpace class="px-6 py-7">
        <ElAvatar :src="bot.icon" :size="32" />
        <ElText class="font-bold" size="large">{{ bot.title }}</ElText>
      </ElSpace>
      <ElButton :icon="ChatDotRound" size="large">
        {{ $t('button.newConversation') }}
      </ElButton>
      <span class="self-start p-6 pb-2 text-sm text-[#969799]">{{
        $t('common.history')
      }}</span>
    </ElAside>
    <ElMain>Main</ElMain>
  </ElContainer>
</template>
