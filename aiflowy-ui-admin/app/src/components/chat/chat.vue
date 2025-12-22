<script setup lang="ts">
import type { BubbleListProps } from 'vue-element-plus-x/types/BubbleList';
import type { TypewriterInstance } from 'vue-element-plus-x/types/Typewriter';

import type { BotInfo, ChatMessage } from '@aiflowy/types';

import { onMounted, ref, watchEffect } from 'vue';
import { BubbleList, Sender } from 'vue-element-plus-x';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';
import { useBotStore } from '@aiflowy/stores';
import { cn, tryit, uuid } from '@aiflowy/utils';

import { CopyDocument, RefreshRight } from '@element-plus/icons-vue';
import { ElButton, ElIcon, ElMessage, ElSpace } from 'element-plus';

import { getMessageList, getPerQuestions } from '#/api';
import { api, sseClient } from '#/api/request';
import MarkdownRenderer from '#/components/chat/MarkdownRenderer.vue';
import SendEnableIcon from '#/components/icons/SendEnableIcon.vue';
import SendIcon from '#/components/icons/SendIcon.vue';

import BotAvatar from '../botAvatar/botAvatar.vue';
import SendingIcon from '../icons/SendingIcon.vue';

const props = defineProps<{
  bot?: BotInfo;
  sessionId?: string;
  // 是否显示对话列表
  showChatConversations?: boolean;
}>();
const botStore = useBotStore();
interface historyMessageType {
  role: string;
  content: string;
}
interface presetQuestionsType {
  key: string;
  description: string;
}
const route = useRoute();
const botId = ref<string>((route.params.id as string) || '');
const router = useRouter();

const bubbleItems = ref<BubbleListProps<ChatMessage>['list']>([]);
const senderRef = ref<InstanceType<typeof Sender>>();
const senderValue = ref('');
const sending = ref(false);
const sessionId = ref(
  props.sessionId && props.sessionId.length > 0 ? props.sessionId : uuid(),
);
const presetQuestions = ref<presetQuestionsType[]>([]);
defineExpose({
  clear() {
    bubbleItems.value = [];
    messages.value = [];
  },
});
const getPresetQuestions = () => {
  api
    .get('/api/v1/aiBot/detail', {
      params: {
        id: botId.value,
      },
    })
    .then((res) => {
      if (res.data.options?.presetQuestions) {
        presetQuestions.value = res.data.options?.presetQuestions
          .filter(
            (item: presetQuestionsType) =>
              item.description && item.description.trim() !== '',
          )
          .map((item: presetQuestionsType) => ({
            key: item.key,
            description: item.description,
          }));
      }
    });
};
const getMessagesHistory = () => {
  if (!props.showChatConversations) {
    return;
  }
  api
    .get('/api/v1/aiBotMessage/list', {
      params: {
        botId: botId.value,
        sessionId: sessionId.value,
      },
    })
    .then((res) => {
      bubbleItems.value = res.data.map((item: ChatMessage) => {
        return item.role === 'assistant'
          ? {
              ...item,
              placement: 'start',
              isAssistant: true,
            }
          : {
              ...item,
              placement: 'end',
              isAssistant: false,
            };
      });
    });
};
onMounted(() => {
  getMessagesHistory();
  getPresetQuestions();
});
watchEffect(async () => {
  if (props.bot && props.sessionId) {
    const [, res] = await tryit(
      getMessageList({
        sessionId: props.sessionId,
        botId: props.bot.id,
        tempUserId: uuid() + props.bot.id,
      }),
    );

    if (res?.errorCode === 0) {
      bubbleItems.value = res.data.map((item) => ({
        ...item,
        content:
          item.role === 'assistant'
            ? item.content.replace(/^Final Answer:\s*/i, '')
            : item.content,
        placement: item.role === 'assistant' ? 'start' : 'end',
      }));
    }
  } else {
    bubbleItems.value = [];
  }
});
const lastUserMessage = ref('');
const messages = ref<historyMessageType[]>([]);
const stopSse = () => {
  sseClient.abort();
  sending.value = false;
  const lastBubbleItem = bubbleItems.value[bubbleItems.value.length - 1];
  if (lastBubbleItem) {
    bubbleItems.value[bubbleItems.value.length - 1] = {
      ...lastBubbleItem,
      content: lastBubbleItem.content,
      loading: false,
      typing: false,
    };
  }
};
const handleSubmit = async (refreshContent: string) => {
  const currentPrompt = refreshContent || senderValue.value.trim();
  if (!currentPrompt) {
    return;
  }
  sending.value = true;
  lastUserMessage.value = currentPrompt;
  if (!props.showChatConversations) {
    messages.value.push({
      role: 'user',
      content: currentPrompt,
    });
  }
  const data = {
    botId: botId.value,
    prompt: currentPrompt,
    sessionId: sessionId.value,
    messages: messages.value,
  };
  const mockMessages = generateMockMessages(refreshContent);
  bubbleItems.value.push(...mockMessages);
  senderRef.value?.clear();

  sseClient.post('/api/v1/aiBot/chat', data, {
    onMessage(message) {
      const event = message.event;
      //  finish
      if (event === 'finish') {
        sending.value = false;
        return;
      }
      const content = message.data!.replace(/^Final Answer:\s*/i, '');
      if (event === 'needSaveMessage' && message.data) {
        const dataObj = JSON.parse(message.data);
        const role = dataObj.role;
        const contentObj = JSON.parse(dataObj.content);
        if (
          messages.value.length > 0 &&
          messages.value[messages.value.length - 1]?.role === 'user' &&
          role === 'user'
        ) {
          messages.value.pop();
        }
        messages.value.push({
          role,
          content: contentObj,
        });
        return;
      }
      const lastBubbleItem = bubbleItems.value[bubbleItems.value.length - 1];

      if (lastBubbleItem) {
        if (content === lastBubbleItem.content) {
          sending.value = false;
        } else {
          bubbleItems.value[bubbleItems.value.length - 1] = {
            ...lastBubbleItem,
            content: lastBubbleItem.content + content,
            loading: false,
            typing: true,
          };
        }
      }
    },
  });
};
const handleComplete = (_: TypewriterInstance, index: number) => {
  if (
    index === bubbleItems.value.length - 1 &&
    props.sessionId &&
    props.sessionId.length <= 0 &&
    sending.value === false
  ) {
    setTimeout(() => {
      router.replace({ params: { sessionId: sessionId.value } });
    }, 100);
  }
};

const generateMockMessages = (refreshContent: string) => {
  const userMessage: ChatMessage = {
    role: 'user',
    id: Date.now().toString(),
    fileList: [],
    content: refreshContent || senderValue.value,
    created: Date.now(),
    updateAt: Date.now(),
    placement: 'end',
  };

  const assistantMessage: ChatMessage = {
    role: 'assistant',
    id: Date.now().toString(),
    content: '',
    loading: true,
    created: Date.now(),
    updateAt: Date.now(),
    placement: 'start',
  };

  return [userMessage, assistantMessage];
};

const handleCopy = (content: string) => {
  navigator.clipboard
    .writeText(content)
    .then(() => ElMessage.success($t('message.copySuccess')))
    .catch(() => ElMessage.error($t('message.copyFail')));
};

const handleRefresh = () => {
  handleSubmit(lastUserMessage.value);
};
// const senderHeader = ref();
// async function handlePasteFile(firstFile: File, fileList: FileList) {
//   if (senderRef.value) {
//     senderRef.value.openHeader();
//     await nextTick();
//     if (senderHeader.value) {
//       senderHeader.value.init(firstFile);
//     }
//   }
// }
</script>

<template>
  <div class="mx-auto h-full max-w-[780px]">
    <div
      :class="
        cn(
          'flex h-full w-full flex-col gap-3',
          !sessionId && 'items-center justify-center gap-8',
        )
      "
    >
      <!-- 对话列表 -->
      <div
        v-if="sessionId || bubbleItems.length > 0"
        class="message-container w-full flex-1 overflow-hidden"
      >
        <BubbleList
          class="!h-full"
          :list="bubbleItems"
          max-height="none"
          @complete="handleComplete"
        >
          <template #header="{ item }">
            <span class="chat-bubble-item-time-style">
              {{ new Date(item.created).toLocaleString() }}
            </span>
          </template>
          <!-- 自定义头像 -->
          <template #avatar="{ item }">
            <BotAvatar
              v-if="item.role === 'assistant'"
              :src="bot?.icon"
              :size="40"
            />
          </template>
          <template #content="{ item }">
            <MarkdownRenderer :content="item.content" />
          </template>
          <!-- 自定义底部 -->
          <template #footer="{ item }">
            <ElSpace :size="10">
              <ElSpace>
                <span @click="handleRefresh()" style="cursor: pointer">
                  <ElIcon>
                    <RefreshRight />
                  </ElIcon>
                </span>
                <span @click="handleCopy(item.content)" style="cursor: pointer">
                  <ElIcon>
                    <CopyDocument />
                  </ElIcon>
                </span>
              </ElSpace>
            </ElSpace>
          </template>
        </BubbleList>
      </div>

      <!-- 新对话显示bot信息 -->
      <div v-else class="flex flex-col items-center gap-3.5">
        <BotAvatar :src="bot?.icon" :size="88" />
        <h1 class="text-base font-medium text-black/85">
          {{ bot?.title }}
        </h1>
        <span class="text-sm text-[#757575]">{{ bot?.description }}</span>
      </div>

      <!--问题预设-->
      <div
        class="questions-preset-container"
        v-if="botStore.presetQuestions.length > 0"
      >
        <ElButton
          v-for="item in getPerQuestions(botStore.presetQuestions)"
          :key="item.key"
          @click="handleSubmit(item.description)"
        >
          {{ item.description }}
        </ElButton>
      </div>
      <!-- Sender -->
      <Sender
        ref="senderRef"
        class="w-full"
        v-model="senderValue"
        variant="updown"
        :auto-size="{ minRows: 3, maxRows: 6 }"
        allow-speech
        @submit="handleSubmit"
      >
        <!-- 自定义头部内容 -->
        <!--        <template #header>
          <div class="header-self-wrap">
            <SenderHeader ref="senderHeader" />
          </div>
        </template>-->

        <template #action-list>
          <ElSpace>
            <!--<ElButton circle @click="uploadRef.triggerFileSelect()">
              <ElIcon><Paperclip /></ElIcon>
            </ElButton>
            <ElButton circle>
              <ElIcon><Microphone /></ElIcon>
              &lt;!&ndash; <ElIcon color="#0066FF"><RecordingIcon /></ElIcon> &ndash;&gt;
            </ElButton>-->
            <ElButton v-if="sending" circle @click="stopSse">
              <ElIcon size="30" color="#409eff"><SendingIcon /></ElIcon>
            </ElButton>
            <template v-else>
              <ElButton v-if="!senderValue" circle disabled>
                <SendIcon />
              </ElButton>
              <ElButton v-else circle @click="handleSubmit('')">
                <SendEnableIcon />
              </ElButton>
            </template>
          </ElSpace>
        </template>
      </Sender>
    </div>
  </div>
</template>

<style scoped>
.questions-preset-container {
  width: 100%;
  display: flex;
  flex-wrap: nowrap;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;
  overflow: auto;
}
.message-container {
  background-color: var(--bot-chat-message-container);
  border-radius: 8px;
  padding: 8px;
}
:deep(.el-bubble-content-wrapper .el-bubble-content-filled[data-v-a52d8fe0]) {
  background-color: var(--bot-chat-message-item-back);
}
.chat-bubble-item-time-style {
  color: var(--common-font-placeholder-color);
  font-size: 12px;
}
</style>
