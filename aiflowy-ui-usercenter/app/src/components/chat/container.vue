<script setup lang="ts">
import { ref, watch } from 'vue';

import { createIconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { ElAside, ElButton, ElContainer, ElHeader, ElMain } from 'element-plus';

import { api } from '#/api/request';
import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';

interface Props {
  bot: any;
  onMessageList?: (list: any[]) => void;
}
const props = defineProps<Props>();
const sessionList = ref<any>([]);
const currentSession = ref<any>({});
watch(
  () => props.bot.id,
  () => {
    getSessionList();
  },
);
function getSessionList() {
  api
    .get('/userCenter/conversation/list', {
      params: {
        botId: props.bot.id,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        sessionList.value = res.data;
      }
    });
}
function addSession() {
  sessionList.value.push({
    sessionId: crypto.randomUUID(),
    title: '新对话',
  });
}
function clickSession(session: any) {
  currentSession.value = session;
  getMessageList();
}
function getMessageList() {
  api
    .get('/userCenter/aiBotMessage/getMessages', {
      params: {
        botId: props.bot.id,
        sessionId: currentSession.value.sessionId,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        props.onMessageList?.(res.data);
      }
    });
}
</script>

<template>
  <ElContainer class="border-border h-full rounded-lg border-2">
    <ElAside width="287px" class="border-border border-r p-6">
      <Card class="max-w-max p-0">
        <CardAvatar />
        <CardContent>
          <CardTitle>{{ bot.title }}</CardTitle>
          <CardDescription>{{ bot.description }}</CardDescription>
        </CardContent>
      </Card>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        color="#0066FF"
        :icon="createIconifyIcon('mdi:chat-plus-outline')"
        plain
        @click="addSession"
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          v-for="session in sessionList"
          :key="session.sessionId"
          :class="
            cn(
              'flex cursor-pointer items-center justify-between rounded-lg px-5 py-2.5 text-sm',
              currentSession.sessionId === session.sessionId
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="clickSession(session)"
        >
          <span
            :class="
              cn(
                'text-foreground',
                currentSession.sessionId === session.sessionId &&
                  'text-primary',
              )
            "
          >
            {{ session.title || '未命名' }}
          </span>
          <span class="text-foreground">
            {{ session.created }}
          </span>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader class="border border-[#E6E9EE] bg-[#FAFAFA]" height="53px">
        <span class="text-base/[53px] font-medium text-[#323233]">
          {{ currentSession.title || '未命名' }}
        </span>
      </ElHeader>
      <ElMain>
        <slot :session-id="currentSession.sessionId"></slot>
      </ElMain>
    </ElContainer>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
