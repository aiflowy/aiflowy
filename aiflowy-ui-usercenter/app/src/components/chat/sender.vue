<script setup lang="ts">
import type { BubbleProps } from 'vue-element-plus-x/types/Bubble';
import type { ThinkingStatus } from 'vue-element-plus-x/types/Thinking';

import { inject, ref } from 'vue';

import { cloneDeep, uuid } from '@aiflowy/utils';

import { Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

import { sseClient } from '#/api/request';
import SendingIcon from '#/components/icons/SendingIcon.vue';
// import PaperclipIcon from '#/components/icons/PaperclipIcon.vue';

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

type MessageItem = BubbleProps & {
  chains?: (Think | Tool)[];
  key: string;
  role: 'assistant' | 'user';
};

interface Props {
  conversationId: string | undefined;
  bot: any;
  addMessage: (message: MessageItem) => void;
  updateLastMessage: (item: any) => void;
  stopThinking: () => void;
}

const props = defineProps<Props>();
const senderValue = ref('');
const btnLoading = ref(false);
const getSessionList = inject<any>('getSessionList');
function sendMessage() {
  if (getDisabled()) {
    return;
  }
  const data = {
    conversationId: props.conversationId,
    prompt: senderValue.value,
    botId: props.bot.id,
  };
  btnLoading.value = true;
  props.addMessage({
    key: uuid(),
    role: 'user',
    placement: 'end',
    content: senderValue.value,
    typing: true,
  });
  props.addMessage({
    key: uuid(),
    role: 'assistant',
    placement: 'start',
    content: '',
    loading: true,
    typing: true,
  });
  senderValue.value = '';

  let content = '';

  sseClient.post('/userCenter/bot/chat', data, {
    onMessage(res) {
      if (!res.data) {
        return;
      }
      const sseData = JSON.parse(res.data);
      const delta = sseData.payload?.delta;

      if (res.event === 'done') {
        btnLoading.value = false;
        getSessionList();
      }

      // 处理系统错误
      if (
        sseData?.domain === 'SYSTEM' &&
        sseData.payload?.code === 'SYSTEM_ERROR'
      ) {
        const errorMessage = sseData.payload.message;
        props.updateLastMessage({
          content: errorMessage,
          loading: false,
          typing: false,
        });
        return;
      }

      if (sseData?.domain === 'TOOL') {
        props.updateLastMessage((message: MessageItem) => {
          const chains = cloneDeep(message.chains ?? []);
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
          return { chains };
        });
        props.stopThinking();
        return;
      }

      if (sseData.type === 'THINKING') {
        props.updateLastMessage((message: MessageItem) => {
          const chains = cloneDeep(message.chains ?? []);
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
          return { chains };
        });
      } else if (sseData.type === 'MESSAGE') {
        props.updateLastMessage({
          thinkingStatus: 'end',
          loading: false,
          content: (content += delta),
        });
        props.stopThinking();
      }
    },
    onError(err) {
      console.error(err);
      btnLoading.value = false;
    },
    onFinished() {
      senderValue.value = '';
      btnLoading.value = false;
      props.updateLastMessage({ loading: false });
      props.stopThinking();
    },
  });
}
const isTool = (item: Think | Tool) => {
  return 'id' in item;
};
const isThink = (item: Think | Tool): item is Think => {
  return !('id' in item);
};
function getDisabled() {
  return !senderValue.value || !props.conversationId;
}
const stopSse = () => {
  sseClient.abort();
  btnLoading.value = false;
};
</script>

<template>
  <ElSender
    v-model="senderValue"
    variant="updown"
    :auto-size="{ minRows: 2, maxRows: 5 }"
    clearable
    allow-speech
    placeholder="发送消息"
    @keyup.enter="sendMessage"
  >
    <!-- 自定义 prefix 前缀 -->
    <!-- <template #prefix>
    </template> -->

    <template #action-list>
      <div class="flex items-center gap-2">
        <!-- <ElButton :icon="PaperclipIcon" link /> -->
        <ElButton v-if="btnLoading" circle @click="stopSse">
          <ElIcon size="30" color="#409eff"><SendingIcon /></ElIcon>
        </ElButton>
        <ElButton
          v-else
          type="primary"
          :icon="Promotion"
          :disabled="getDisabled()"
          @click="sendMessage"
          round
        />
      </div>
    </template>
  </ElSender>
</template>
