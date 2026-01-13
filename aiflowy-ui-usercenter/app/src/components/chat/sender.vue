<script setup lang="ts">
import { inject, ref } from 'vue';

import { uuid } from '@aiflowy/utils';

import { Promotion } from '@element-plus/icons-vue';
import { ElButton, ElIcon } from 'element-plus';

import { sseClient } from '#/api/request';
import SendingIcon from '#/components/icons/SendingIcon.vue';
// import PaperclipIcon from '#/components/icons/PaperclipIcon.vue';

interface Props {
  conversationId: string | undefined;
  bot: any;
  addMessage?: (message: any) => void;
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
  senderValue.value = '';
  const msgKey = uuid();
  let str = '';
  sseClient.post('/userCenter/bot/chat', data, {
    onMessage(res) {
      if (!res.data) {
        return;
      }
      const sseData = JSON.parse(res.data);
      const delta = sseData.payload?.delta;
      const msg = {
        key: msgKey,
        role: 'assistant',
        placement: 'start',
        content: (str += delta),
        typing: true,
      };
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
          key: msgKey,
          role: 'assistant',
          placement: 'start',
          content: errorMessage,
          typing: false,
        });
        return;
      }

      if (str !== res.data && res.event !== 'done') {
        props.addMessage?.(msg);
      }
    },
    onError(err) {
      console.error(err);
      btnLoading.value = false;
    },
    onFinished() {
      senderValue.value = '';
      btnLoading.value = false;
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
