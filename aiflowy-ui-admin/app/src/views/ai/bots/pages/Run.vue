<script setup lang="ts">
import type { BotInfo, Session } from '@aiflowy/types';

import { onMounted, ref, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';
import { preferences } from '@aiflowy/preferences';
import { uuid } from '@aiflowy/utils';

import {
  ElAside,
  ElButton,
  ElContainer,
  ElEmpty,
  ElMain,
  ElSpace,
} from 'element-plus';
import { tryit } from 'radash';

import { getBotDetails, getSessionList } from '#/api';
import BotAvatar from '#/components/botAvatar/botAvatar.vue';
import Chat from '#/components/chat/chat.vue';
import { $t } from '#/locales';

const route = useRoute();
const router = useRouter();

const bot = ref<BotInfo>();
const sessionList = ref<Session[]>([]);
const sessionId = ref<string>(route.params.sessionId as string);

watchEffect(() => {
  sessionId.value = route.params.sessionId as string;
});

// 内置菜单点击方法
// function handleMenuCommand(
//   command: ConversationMenuCommand,
//   item: ConversationItem,
// ) {
//   console.warn('内置菜单点击事件：', command, item);
//   // 直接修改 item 是否生效
//   if (command === 'delete') {
//     const index = menuTestItems.value.findIndex(
//       (itemSlef) => itemSlef.key === item.key,
//     );

//     if (index !== -1) {
//       menuTestItems.value.splice(index, 1);
//       console.warn('删除成功');
//       ElMessage.success('删除成功');
//     }
//   }
//   if (command === 'rename') {
//     item.label = '已修改';
//     console.warn('重命名成功');
//     ElMessage.success('重命名成功');
//   }
// }

onMounted(() => {
  if (route.params.botId) {
    fetchBotDetail(route.params.botId as string);
    fetchSessionList(route.params.botId as string);
  }
});

const fetchBotDetail = async (id: string) => {
  const [, res] = await tryit(getBotDetails)(id);

  if (res?.errorCode === 0) {
    bot.value = res.data;
  }
};
const fetchSessionList = async (id: string) => {
  const [, res] = await tryit(getSessionList)({
    botId: id,
    tempUserId: uuid().toString() + id,
  });

  if (res?.errorCode === 0) {
    sessionList.value = res.data.cons;
  }
};

const updateActive = (_sessionId?: number | string) => {
  sessionId.value = `${_sessionId ?? ''}`;
  router.push(
    `/ai/bots/run/${bot.value?.id}${_sessionId ? `/${_sessionId}` : ''}`,
  );
};
</script>

<template>
  <ElContainer class="h-screen" v-if="bot">
    <ElAside width="240px" class="flex flex-col items-center bg-[#f5f5f580]">
      <ElSpace class="py-7">
        <BotAvatar :src="bot.icon" :size="40" />
        <span class="text-base font-medium text-black/85">{{ bot.title }}</span>
      </ElSpace>
      <ElButton
        type="primary"
        class="!h-10 w-full max-w-[208px]"
        plain
        @click="updateActive()"
      >
        <template #icon>
          <IconifyIcon icon="mdi:chat-outline" />
        </template>
        {{ $t('button.newConversation') }}
      </ElButton>
      <span class="self-start p-6 pb-2 text-sm text-[#969799]">{{
        $t('common.history')
      }}</span>
      <div class="w-full max-w-[208px] flex-1 overflow-hidden">
        <ElConversations
          v-show="sessionList.length > 0"
          class="!w-full !shadow-none"
          v-model:active="sessionId"
          :items="sessionList"
          :label-max-width="120"
          :show-tooltip="true"
          row-key="sessionId"
          label-key="title"
          tooltip-placement="right"
          :tooltip-offset="35"
          show-to-top-btn
          show-built-in-menu
          show-built-in-menu-type="hover"
          @update:active="updateActive"
        />
        <ElEmpty
          :image="`/empty${preferences.theme.mode === 'dark' ? '-dark' : ''}.png`"
          v-show="sessionList.length === 0"
        />
      </div>
    </ElAside>
    <ElMain>
      <Chat :session-id="sessionId" :bot="bot" />
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.conversations-container :deep(.conversations-list) {
  width: 100% !important;
  padding: 0 !important;
  background: none !important;
}

.conversations-container :deep(.conversation-item) {
  margin: 0;
}

.conversations-container :deep(.conversation-label) {
  color: #1a1a1a;
}
</style>
