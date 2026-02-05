<script setup lang="ts">
import type { Sender } from 'vue-element-plus-x';
import type { BubbleListProps } from 'vue-element-plus-x/types/BubbleList';
import type { ThinkingStatus } from 'vue-element-plus-x/types/Thinking';
import type { TypewriterInstance } from 'vue-element-plus-x/types/Typewriter';

import type { BotInfo, ChatMessage } from '@aiflowy/types';

import { onMounted, ref, watchEffect } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';
import { $t } from '@aiflowy/locales';
import { useBotStore } from '@aiflowy/stores';
import { cloneDeep, cn, uuid } from '@aiflowy/utils';

import {
  CircleCheck,
  CopyDocument,
  RefreshRight,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElCollapse,
  ElCollapseItem,
  ElIcon,
  ElMessage,
  ElSpace,
} from 'element-plus';
import { tryit } from 'radash';

import { getMessageList, getPerQuestions } from '#/api';
import { api, sseClient } from '#/api/request';
import SendEnableIcon from '#/components/icons/SendEnableIcon.vue';
import SendIcon from '#/components/icons/SendIcon.vue';
import ShowJson from '#/components/json/ShowJson.vue';

import BotAvatar from '../botAvatar/botAvatar.vue';
import SendingIcon from '../icons/SendingIcon.vue';

type Think = {
  reasoning_content?: string;
  thinkingStatus?: ThinkingStatus;
  thinlCollapse?: boolean;
};

type Tool = {
  id: string;
  name: string;
  result: string;
  status: 'TOOL_CALL' | 'TOOL_RESULT';
};

type MessageItem = ChatMessage & {
  chains?: (Think | Tool)[];
};

const props = defineProps<{
  bot?: BotInfo;
  conversationId?: string;
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

const bubbleItems = ref<BubbleListProps<MessageItem>['list']>([]);
const senderRef = ref<InstanceType<typeof Sender>>();
const senderValue = ref('');
const sending = ref(false);
const getConversationId = async () => {
  const res = await api.get('/api/v1/bot/generateConversationId');
  return res.data;
};
const localeConversationId = ref<any>('');

const presetQuestions = ref<presetQuestionsType[]>([]);
defineExpose({
  clear() {
    bubbleItems.value = [];
    messages.value = [];
  },
});
const getPresetQuestions = () => {
  api
    .get('/api/v1/bot/detail', {
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
onMounted(async () => {
  // 初始化 conversationId
  localeConversationId.value =
    props.conversationId && props.conversationId.length > 0
      ? props.conversationId
      : await getConversationId();
  getPresetQuestions();
});
watchEffect(async () => {
  if (props.bot && props.conversationId) {
    const [, res] = await tryit(getMessageList)({
      conversationId: props.conversationId,
      botId: props.bot.id,
      tempUserId: uuid() + props.bot.id,
    });

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
  messages.value.push({
    role: 'user',
    content: currentPrompt,
  });
  const copyMessages = [...messages.value];
  const data = {
    botId: botId.value,
    prompt: currentPrompt,
    conversationId: localeConversationId.value,
    messages: copyMessages,
  };
  messages.value.pop();
  const mockMessages = generateMockMessages(refreshContent);
  bubbleItems.value.push(...mockMessages);
  senderRef.value?.clear();
  sseClient.post('/api/v1/bot/chat', data, {
    onMessage(message) {
      const event = message.event;
      const lastIndex = bubbleItems.value.length - 1;
      const lastBubbleItem = bubbleItems.value[lastIndex];

      //  finish
      if (event === 'done') {
        sending.value = false;
        return;
      }
      if (!message.data) {
        return;
      }
      // 处理系统错误
      const sseData = JSON.parse(message.data);
      if (
        sseData?.domain === 'SYSTEM' &&
        sseData.payload?.code === 'SYSTEM_ERROR'
      ) {
        const errorMessage = sseData.payload.message;
        if (!lastBubbleItem) return;
        bubbleItems.value[lastIndex] = {
          ...lastBubbleItem,
          content: errorMessage,
          loading: false,
          typing: true,
        };
        return;
      }

      if (lastIndex >= 0 && sseData?.domain === 'TOOL') {
        const chains = cloneDeep(lastBubbleItem?.chains ?? []);
        const index = chains.findIndex(
          (chain) =>
            isTool(chain) && chain.id === sseData?.payload?.tool_call_id,
        );

        if (index === -1) {
          chains.push({
            id: sseData?.payload?.tool_call_id,
            name: sseData?.payload?.name,
            status: sseData?.type,
            result:
              sseData?.type === 'TOOL_CALL'
                ? sseData?.payload?.arguments
                : sseData?.payload?.result,
          });
        } else {
          chains[index] = {
            ...chains[index]!,
            status: sseData?.type,
            result:
              sseData?.type === 'TOOL_CALL'
                ? sseData?.payload?.arguments
                : sseData?.payload?.result,
          };
        }
        bubbleItems.value[lastIndex]!.chains = chains;
        stopThinking();
        return;
      }

      // 处理流式消息
      const delta = sseData.payload?.delta;
      const role = sseData.payload?.role;

      if (lastBubbleItem && delta) {
        if (sseData.type === 'THINKING') {
          const chains = cloneDeep(lastBubbleItem?.chains ?? []);
          const index = chains.findIndex(
            (chain) => isThink(chain) && chain.thinkingStatus === 'thinking',
          );

          if (index === -1) {
            chains.push({
              thinkingStatus: 'thinking',
              thinlCollapse: true,
              reasoning_content: delta,
            });
          } else {
            const think = chains[index]! as Think;
            chains[index] = {
              ...think,
              reasoning_content: think.reasoning_content + delta,
            };
          }
          bubbleItems.value[lastIndex]!.chains = chains;
        } else if (sseData.type === 'MESSAGE') {
          bubbleItems.value[lastIndex] = {
            ...lastBubbleItem,
            content: (lastBubbleItem.content + delta).replaceAll(
              '```echartsoption',
              '```echarts\noption',
            ),
            loading: false,
            typing: true,
          };
          stopThinking();
        }
      }

      // 是否需要保存聊天记录
      if (event === 'needSaveMessage') {
        messages.value.push({
          role,
          content: sseData.payload?.content,
        });
      }
    },
    onFinished() {
      sending.value = false;

      const lastIndex = bubbleItems.value.length - 1;
      if (lastIndex) {
        bubbleItems.value[lastIndex] = {
          ...bubbleItems.value[lastIndex]!,
          loading: false,
        };
      }
      stopThinking();
    },
    onError(err) {
      console.error(err);
      sending.value = false;
    },
  });
};

const isTool = (item: Think | Tool) => {
  return 'id' in item;
};
const isThink = (item: Think | Tool): item is Think => {
  return !('id' in item);
};
const stopThinking = () => {
  const lastIndex = bubbleItems.value.length - 1;

  if (lastIndex >= 0 && bubbleItems.value[lastIndex]?.chains) {
    const chains = cloneDeep(bubbleItems.value[lastIndex].chains);

    for (const chain of chains) {
      if (isThink(chain) && chain.thinkingStatus === 'thinking') {
        chain.thinkingStatus = 'end';
      }
    }

    bubbleItems.value[lastIndex].chains = chains;
  }
};

const handleComplete = (_: TypewriterInstance, index: number) => {
  if (
    index === bubbleItems.value.length - 1 &&
    props.conversationId &&
    props.conversationId.length <= 0 &&
    sending.value === false
  ) {
    setTimeout(() => {
      router.replace({
        params: { conversationId: localeConversationId.value },
      });
    }, 100);
  }
};

const generateMockMessages = (refreshContent: string) => {
  const userMessage: MessageItem = {
    role: 'user',
    id: Date.now().toString(),
    fileList: [],
    content: refreshContent || senderValue.value,
    created: Date.now(),
    updateAt: Date.now(),
    placement: 'end',
  };

  const assistantMessage: MessageItem = {
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
</script>

<template>
  <div class="mx-auto h-full max-w-[780px]">
    <div
      :class="
        cn(
          'flex h-full w-full flex-col gap-3',
          !localeConversationId && 'items-center justify-center gap-8',
        )
      "
    >
      <!-- 对话列表 -->
      <div
        v-if="localeConversationId || bubbleItems.length > 0"
        class="message-container w-full flex-1 overflow-hidden"
      >
        <ElBubbleList
          class="!h-full"
          max-height="none"
          :list="bubbleItems"
          @complete="handleComplete"
        >
          <template #header="{ item }">
            <div class="flex flex-col">
              <span class="chat-bubble-item-time-style">
                {{ new Date(item.created).toLocaleString() }}
              </span>

              <template v-if="item.chains">
                <template
                  v-for="(chain, index) in item.chains"
                  :key="chain.id || index"
                >
                  <ElThinking
                    v-if="isThink(chain)"
                    v-model="chain.thinlCollapse"
                    :content="chain.reasoning_content"
                    :status="chain.thinkingStatus"
                  />
                  <ElCollapse v-else class="mb-2">
                    <ElCollapseItem :title="chain.name" :name="chain.id">
                      <template #title>
                        <div class="flex items-center gap-2 pl-5">
                          <ElIcon size="16">
                            <IconifyIcon icon="svg:wrench" />
                          </ElIcon>
                          <span>{{ chain.name }}</span>
                          <template v-if="chain.status === 'TOOL_CALL'">
                            <div
                              class="bg-secondary flex items-center gap-1 rounded-full px-2 py-0.5 leading-none"
                            >
                              <ElIcon size="16">
                                <IconifyIcon
                                  icon="mdi:clock-time-five-outline"
                                />
                              </ElIcon>
                              <span>{{ $t('bot.Running') }}...</span>
                            </div>
                          </template>
                          <template v-else>
                            <div
                              class="bg-secondary flex items-center gap-1 rounded-full px-2 py-0.5 leading-none"
                            >
                              <ElIcon size="16" color="var(--el-color-success)">
                                <CircleCheck />
                              </ElIcon>
                              <span>{{ $t('bot.Completed') }}</span>
                            </div>
                          </template>
                        </div>
                      </template>
                      <ShowJson :value="chain.result" />
                    </ElCollapseItem>
                  </ElCollapse>
                </template>
              </template>

              <!-- <ElThinking
                v-if="item.reasoning_content"
                v-model="item.thinlCollapse"
                :content="item.reasoning_content"
                :status="item.thinkingStatus"
                class="mb-3"
              /> -->
              <!-- <ElCollapse v-if="item.tools" class="mb-2">
                <ElCollapseItem
                  class="mb-2"
                  v-for="tool in item.tools"
                  :key="tool.id"
                  :title="tool.name"
                  :name="tool.id"
                >
                  <template #title>
                    <div class="flex items-center gap-2 pl-5">
                      <ElIcon size="16">
                        <IconifyIcon icon="svg:wrench" />
                      </ElIcon>
                      <span>{{ tool.name }}</span>
                      <template v-if="tool.status === 'TOOL_CALL'">
                        <ElIcon size="16">
                          <IconifyIcon icon="svg:spinner" />
                        </ElIcon>
                      </template>
                      <template v-else>
                        <ElIcon size="16" color="var(--el-color-success)">
                          <CircleCheck />
                        </ElIcon>
                      </template>
                    </div>
                  </template>
                  <ShowJson :value="tool.result" />
                </ElCollapseItem>
              </ElCollapse> -->
            </div>
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
            <ElXMarkdown :markdown="item.content" />
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
        </ElBubbleList>
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
      <ElSender
        ref="senderRef"
        class="w-full"
        v-model="senderValue"
        :placeholder="$t('message.pleaseInputContent')"
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
      </ElSender>
    </div>
  </div>
</template>

<style scoped>
.questions-preset-container {
  display: flex;
  flex-flow: row nowrap;
  gap: 10px;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  overflow: auto;
}

.message-container {
  padding: 8px;
  background-color: var(--bot-chat-message-container);
  border-radius: 8px;
}

.dark .message-container {
  border: 1px solid hsl(var(--border));
}

:deep(.el-bubble-content-wrapper .el-bubble-content-filled[data-v-a52d8fe0]) {
  background-color: var(--bot-chat-message-item-back);
}

.chat-bubble-item-time-style {
  font-size: 12px;
  color: var(--common-font-placeholder-color);
}

.el-bubble-list :deep(.el-bubble.el-bubble-start) {
  --bubble-content-max-width: calc(
    100% - var(--el-bubble-avatar-placeholder-gap)
  ) !important;
}

.el-bubble-list :deep(.el-bubble.el-bubble-end) {
  --bubble-content-max-width: calc(
    100% -
      calc(
        var(--el-bubble-avatar-placeholder-gap) + var(--el-avatar-size, 40px)
      )
  ) !important;
}

:deep(.el-bubble-header) {
  width: 100%;
}

:deep(.el-bubble-end .el-bubble-header) {
  width: fit-content;
}

:deep(.el-thinking) {
  margin: 0;
}

:deep(.el-thinking .content-wrapper) {
  --el-thinking-content-wrapper-width: var(--bubble-content-max-width);

  margin-bottom: 8px;
}

:deep(.el-collapse-item) {
  overflow: hidden;
  border-radius: 8px;
}

:deep(.el-collapse-item__content) {
  padding-bottom: 0;
}
</style>
