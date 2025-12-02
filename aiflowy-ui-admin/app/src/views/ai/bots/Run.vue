<script setup lang="ts">
import type {
  ConversationItem,
  ConversationMenuCommand,
} from 'vue-element-plus-x/types/Conversations';

import type { BotInfo } from '@aiflowy/types';

import { onBeforeMount, ref } from 'vue';
import { Conversations } from 'vue-element-plus-x';
import { useRoute } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';

import {
  ElAside,
  ElAvatar,
  ElButton,
  ElContainer,
  ElMain,
  ElMessage,
  ElSpace,
} from 'element-plus';

import { getBotDetails } from '#/api';
import Chat from '#/components/chat/chat.vue';
import { $t } from '#/locales';

const route = useRoute();
const bot = ref<BotInfo>();

const menuTestItems = ref([
  {
    key: 'm1',
    label: '菜单测试项目 1 - 长文本效果演示文本长度溢出效果测试'.repeat(2),
  },
  {
    key: 'm2',
    label: '菜单测试项目 2',
    disabled: true,
  },
  {
    key: 'm3',
    label: '菜单测试项目 3',
  },
  {
    key: 'm4',
    label: '菜单测试项目 4',
  },
  {
    key: 'm5',
    label: '菜单测试项目 5',
  },
  {
    key: 'm6',
    label: '菜单测试项目 6',
  },
  {
    key: 'm7',
    label: '菜单测试项目 7',
  },
  {
    key: 'm8',
    label: '菜单测试项目 8',
  },
  {
    key: 'm9',
    label: '菜单测试项目 9',
  },
  {
    key: 'm10',
    label: '菜单测试项目 10',
  },
  {
    key: 'm11',
    label: '菜单测试项目 11',
  },
  {
    key: 'm12',
    label: '菜单测试项目 12',
  },
  {
    key: 'm13',
    label: '菜单测试项目 13',
  },
  {
    key: 'm14',
    label: '菜单测试项目 14',
  },
]);
const activeKey4 = ref('m1');

// 内置菜单点击方法
function handleMenuCommand(
  command: ConversationMenuCommand,
  item: ConversationItem,
) {
  console.warn('内置菜单点击事件：', command, item);
  // 直接修改 item 是否生效
  if (command === 'delete') {
    const index = menuTestItems.value.findIndex(
      (itemSlef) => itemSlef.key === item.key,
    );

    if (index !== -1) {
      menuTestItems.value.splice(index, 1);
      console.warn('删除成功');
      ElMessage.success('删除成功');
    }
  }
  if (command === 'rename') {
    item.label = '已修改';
    console.warn('重命名成功');
    ElMessage.success('重命名成功');
  }
}

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
    <ElAside width="240px" class="flex flex-col items-center bg-[#f5f5f580]">
      <ElSpace class="py-7">
        <ElAvatar :src="bot.icon" :size="40" />
        <span class="text-base font-medium text-black/85">{{ bot.title }}</span>
      </ElSpace>
      <ElButton type="primary" class="!h-10 w-full max-w-[208px]" plain>
        <template #icon>
          <IconifyIcon icon="mdi:chat-outline" />
        </template>
        {{ $t('button.newConversation') }}
      </ElButton>
      <span class="self-start p-6 pb-2 text-sm text-[#969799]">{{
        $t('common.history')
      }}</span>
      <div class="w-full max-w-[208px] flex-1 overflow-hidden">
        <Conversations
          class="!w-full !shadow-none"
          v-model:active="activeKey4"
          :items="menuTestItems"
          :label-max-width="120"
          :show-tooltip="true"
          row-key="key"
          tooltip-placement="right"
          :tooltip-offset="35"
          show-to-top-btn
          show-built-in-menu
          show-built-in-menu-type="hover"
          @menu-command="handleMenuCommand"
        />
      </div>
    </ElAside>
    <ElMain>
      <Chat />
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
