<script setup lang="ts">
import type { BubbleProps } from 'vue-element-plus-x/types/Bubble';
import type { ThinkingStatus } from 'vue-element-plus-x/types/Thinking';

import { inject, ref } from 'vue';

import { uuid } from '@aiflowy/utils';

import { Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

import { sseClient } from '#/api/request';
import SendingIcon from '#/components/icons/SendingIcon.vue';
// import PaperclipIcon from '#/components/icons/PaperclipIcon.vue';

type MessageItem = BubbleProps & {
  key: string;
  reasoning_content?: string;
  role: 'assistant' | 'user';
  thinkingStatus?: ThinkingStatus;
  thinlCollapse?: boolean;
};

interface Props {
  conversationId: string | undefined;
  bot: any;
  addMessage?: (message: MessageItem) => void;
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
  props.addMessage?.({
    key: uuid(),
    role: 'user',
    placement: 'end',
    content: senderValue.value,
    typing: true,
  });
  const assistantMsg: MessageItem = {
    key: uuid(),
    role: 'assistant',
    placement: 'start',
    content: '',
    loading: true,
    typing: true,
    thinlCollapse: true,
  };
  props.addMessage?.(assistantMsg);
  senderValue.value = '';

  let content = '';
  let reasoning_content = '';
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
        props.addMessage?.({
          ...assistantMsg,
          content: errorMessage,
          loading: false,
          typing: false,
        });
        return;
      }

      if (sseData.type === 'THINKING') {
        props.addMessage?.({
          ...assistantMsg,
          thinkingStatus: 'thinking',
          reasoning_content: (reasoning_content += delta),
        });
      } else if (sseData.type === 'MESSAGE') {
        props.addMessage?.({
          ...assistantMsg,
          reasoning_content,
          thinkingStatus: 'end',
          content: (content += delta),
          loading: content.length <= 0,
        });
      }
    },
    onError(err) {
      console.error(err);
      btnLoading.value = false;
    },
    onFinished() {
      senderValue.value = '';
      btnLoading.value = false;

      props.addMessage?.({
        ...assistantMsg,
        content,
        reasoning_content,
        thinkingStatus: 'end',
        thinlCollapse: false,
        loading: false,
      });
    },
  });
}
function getDisabled() {
  return !senderValue.value || !props.conversationId;
}
const stopSse = () => {
  sseClient.abort();
  btnLoading.value = false;
};
</script>

<template>
  <el-sender
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
  </el-sender>
</template>
