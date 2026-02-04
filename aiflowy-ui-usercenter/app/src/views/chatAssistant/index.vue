<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { ElAside, ElContainer, ElMain } from 'element-plus';

import { api } from '#/api/request';
import defaultAssistantAvatar from '#/assets/defaultAssistantAvatar.svg';
import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';
import { ChatBubbleList, ChatContainer, ChatSender } from '#/components/chat';

onMounted(() => {
  getAssistantList();
});
const recentUsedAssistant = ref<any[]>([]);
const currentBot = ref<any>({});
const handleSelectAssistant = (bot: any) => {
  currentBot.value = bot;
  messageList.value = [];
};
function getAssistantList() {
  api.get('/userCenter/botRecentlyUsed/getRecentlyBot').then((res) => {
    recentUsedAssistant.value = res.data;
    if (recentUsedAssistant.value.length > 0) {
      currentBot.value = recentUsedAssistant.value[0];
    }
  });
}
const messageList = ref<any>([]);
function addMessage(message: any) {
  messageList.value.push(message);
}
function updateLastMessage(message: any) {
  const lastIndex = messageList.value.length - 1;

  if (lastIndex >= 0) {
    messageList.value[lastIndex] = {
      ...messageList.value[lastIndex],
      ...message,
    };
  }
}
function setMessageList(messages: any) {
  messageList.value = messages;
}
const isFold = ref(false);
const toggleFold = () => {
  isFold.value = !isFold.value;
};
</script>

<template>
  <div class="bg-background-deep h-full w-full p-6">
    <ElContainer
      class="bg-background border-border h-full overflow-hidden rounded-lg border"
    >
      <ElMain class="!p-0">
        <ChatContainer
          class="border-none"
          :bot="currentBot"
          :is-fold="isFold"
          :on-message-list="setMessageList"
          :toggle-fold="toggleFold"
        >
          <template #default="{ conversationId }">
            <div class="flex h-full flex-col justify-between">
              <ChatBubbleList :bot="currentBot" :messages="messageList" />
              <div class="mx-auto w-full max-w-[1000px]">
                <ChatSender
                  :add-message="addMessage"
                  :update-last-message="updateLastMessage"
                  :bot="currentBot"
                  :conversation-id="conversationId"
                />
              </div>
            </div>
          </template>
        </ChatContainer>
      </ElMain>
      <transition name="collapse-horizontal">
        <ElAside
          v-if="!isFold"
          width="283px"
          class="bg-background border-border flex flex-col gap-5 border-l p-5 pt-4"
        >
          <div class="flex items-center justify-between">
            <span class="pl-2.5 text-base font-medium">助理</span>
            <IconifyIcon
              icon="svg:assistant-fold"
              class="cursor-pointer"
              @click="toggleFold"
            />
          </div>
          <div class="flex h-full flex-col gap-5 overflow-auto">
            <Card
              v-for="assistant in recentUsedAssistant"
              :key="assistant.id"
              :class="
                cn(
                  currentBot.id === assistant.id
                    ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                    : 'hover:bg-[hsl(var(--accent))]',
                )
              "
              @click="handleSelectAssistant(assistant)"
            >
              <CardAvatar
                :src="assistant.icon"
                :default-avatar="defaultAssistantAvatar"
              />
              <CardContent>
                <CardTitle
                  :title="assistant.title"
                  :class="cn(assistant.checked && 'text-primary')"
                >
                  {{ assistant.title }}
                </CardTitle>
                <CardDescription :title="assistant.description">
                  {{ assistant.description }}
                </CardDescription>
              </CardContent>
            </Card>
          </div>
        </ElAside>
      </transition>
    </ElContainer>
  </div>
</template>

<style lang="css" scoped>
.el-aside::-webkit-scrollbar {
  display: none;
}

.collapse-horizontal-enter-active,
.collapse-horizontal-leave-active {
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.collapse-horizontal-enter-from,
.collapse-horizontal-leave-to {
  max-width: 0;
  padding: 0;
  opacity: 0;
  transform-origin: left;
}

.collapse-horizontal-enter-to,
.collapse-horizontal-leave-from {
  max-width: 283px;
  opacity: 1;
  transform-origin: left;
}
</style>
