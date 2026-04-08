<script setup lang="ts">
import { computed, onMounted, ref, useTemplateRef } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';
import { cloneDeep, cn } from '@aiflowy/utils';

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
const bubbleListRef = useTemplateRef<any>('bubbleListRef');
const presetMessage = ref('');
const presetSendTrigger = ref(0);
const showQuestions = computed(() => {
  const list = messageList.value.filter(
    (message: any) => message?.content && message.content.length > 0,
  );
  return list.length === 0;
});
const getPerQuestions = (presetQuestions: any[]) => {
  if (!presetQuestions) {
    return [];
  }
  return presetQuestions
    .filter((item: any) => {
      return (
        typeof item.description === 'string' && item.description.trim() !== ''
      );
    })
    .map((item: any) => ({
      key: item.key,
      description: item.description,
    }));
};
function handlePresetSubmit(content: string) {
  if (!content?.trim()) {
    return;
  }
  presetMessage.value = content;
  presetSendTrigger.value += 1;
}
function addMessage(message: any) {
  messageList.value.push(message);
}
function updateLastMessage(item: any) {
  const lastIndex = messageList.value.length - 1;
  let message = item;

  if (typeof item === 'function') {
    message = item(messageList.value[lastIndex]);
  }

  if (lastIndex >= 0) {
    messageList.value[lastIndex] = {
      ...messageList.value[lastIndex],
      ...message,
    };
  }
}
const stopThinking = () => {
  const lastIndex = messageList.value.length - 1;

  if (lastIndex >= 0 && messageList.value[lastIndex]?.chains) {
    const chains = cloneDeep(messageList.value[lastIndex].chains);

    for (const chain of chains) {
      if (!('id' in chain) && chain.thinkingStatus === 'thinking') {
        chain.thinkingStatus = 'end';
        chain.thinkCollapse = false;
      }
    }

    messageList.value[lastIndex].chains = chains;
  }
  bubbleListRef.value?.scrollBottom();
};
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
            <div
              class="mx-auto flex h-full max-w-[1000px] flex-col justify-between"
            >
              <ChatBubbleList
                ref="bubbleListRef"
                :bot="currentBot"
                :messages="messageList"
              />
              <div class="w-full">
                <div
                  class="questions-preset-container"
                  v-if="
                    getPerQuestions(currentBot?.options?.presetQuestions)
                      .length > 0 && showQuestions
                  "
                >
                  <ElButton
                    v-for="item in getPerQuestions(
                      currentBot?.options?.presetQuestions,
                    )"
                    :key="item.key"
                    @click="handlePresetSubmit(item.description)"
                  >
                    {{ item.description }}
                  </ElButton>
                </div>
                <ChatSender
                  :add-message="addMessage"
                  :update-last-message="updateLastMessage"
                  :stop-thinking="stopThinking"
                  :bot="currentBot"
                  :conversation-id="conversationId"
                  :external-send-message="presetMessage"
                  :external-send-trigger="presetSendTrigger"
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
            <span class="pl-2.5 text-base font-medium">智能体</span>
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
.questions-preset-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
  width: 100%;
  margin-bottom: 10px;
  overflow: hidden;
}

.questions-preset-container :deep(.el-button) {
  max-width: 100%;
  height: auto;
}

.questions-preset-container :deep(.el-button span) {
  display: -webkit-box;
  max-height: 33.59px;
  overflow: hidden;
  -webkit-line-clamp: 2;
  line-height: 1.2;
  text-align: left;
  text-wrap: wrap;
  -webkit-box-orient: vertical;
}

:deep(.el-button + .el-button) {
  margin-left: 0;
}

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
