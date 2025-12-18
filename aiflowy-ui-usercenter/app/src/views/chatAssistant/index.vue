<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { cn } from '@aiflowy/utils';

import { ElAside, ElContainer, ElMain, ElSpace } from 'element-plus';

import { api } from '#/api/request';
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
};
function getAssistantList() {
  api.get('/userCenter/aiBotRecentlyUsed/getRecentlyBot').then((res) => {
    recentUsedAssistant.value = res.data;
    if (recentUsedAssistant.value.length > 0) {
      currentBot.value = recentUsedAssistant.value[0];
    }
  });
}
const messageList = ref<any>([]);
function addMessage(message: any) {
  console.log('addMessage', message)
  // 查找是否有相同key，有就替换，没有就添加，messageList是一个对象数组
  const index = messageList.value.findIndex(
    (item: any) => item.key === message.key,
  );
  if (index === -1) {
    messageList.value.push(message);
  } else {
    messageList.value[index] = message;
  }
}
function setMessageList(messages: any) {
  messageList.value = messages;
}
</script>

<template>
  <ElContainer class="bg-background h-full">
    <ElAside width="283px">
      <ElSpace
        class="p-5"
        direction="vertical"
        alignment="flex-start"
        :size="20"
      >
        <span class="text-foreground pl-5 text-sm">最近使用</span>
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
            <CardAvatar />
            <CardContent>
              <CardTitle :class="cn(assistant.checked && 'text-primary')">
                {{ assistant.title }}
              </CardTitle>
              <CardDescription>
                {{ assistant.description }}
              </CardDescription>
            </CardContent>
          </Card>
        </div>
      </ElSpace>
    </ElAside>
    <ElMain class="p-6 pl-0">
      <ChatContainer :bot="currentBot" :on-message-list="setMessageList">
        <template #default="{ sessionId }">
          <div class="flex h-full flex-col justify-between">
            <ChatBubbleList :messages="messageList" />
            <ChatSender
              :add-message="addMessage"
              :bot="currentBot"
              :session-id="sessionId"
            />
          </div>
        </template>
      </ChatContainer>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-aside::-webkit-scrollbar {
  display: none;
}
</style>
