<script setup lang="ts">
import type { BubbleListProps } from 'vue-element-plus-x/types/BubbleList';

import type { BotInfo, Message } from '@aiflowy/types';

import { onMounted, onUnmounted, ref, watchEffect } from 'vue';
import { BubbleList, Sender } from 'vue-element-plus-x';

import { useUserStore } from '@aiflowy/stores';
import { cn, tryit, uuid } from '@aiflowy/utils';

import { CircleClose, DocumentCopy, Refresh } from '@element-plus/icons-vue';
import { ElAvatar, ElButton, ElIcon } from 'element-plus';

import { getMessageList } from '#/api';
import { sse } from '#/api/request';

import BotAvatar from '../botAvatar/botAvatar.vue';

const props = defineProps<{
  bot?: BotInfo;
  sessionId?: string;
}>();
const { stop, postSse } = sse();
const userStore = useUserStore();
const bubbleItems = ref<BubbleListProps<Message>['list']>([]);

watchEffect(async () => {
  if (props.bot && props.sessionId) {
    const [, res] = await tryit(
      getMessageList({
        sessionId: props.sessionId,
        botId: props.bot.id,
        tempUserId: uuid() + props.bot.id,
        isExternalMsg: 1,
      }),
    );

    if (res?.errorCode === 0) {
      bubbleItems.value = res.data.map((item) => ({
        ...item,
        content:
          item.role === 'assistant'
            ? item.content.replace('Final Answer: ', '')
            : item.content,
        placement: item.role === 'assistant' ? 'start' : 'end',
        noStyle: true,
      }));
    }
  }
});

const senderRef = ref();
const senderValue = ref('');
const showHeaderFlog = ref(false);

onMounted(() => {
  showHeaderFlog.value = true;
  senderRef.value.openHeader();

  // if (props.bot && props.sessionId) {
  //   postSse(
  //     '/api/v1/aiBot/chat',
  //     {
  //       botId: props.bot.id,
  //       fileList: [],
  //       isExternalMsg: 1,
  //       prompt: '你好',
  //       sessionId: props.sessionId,
  //       tempUserId: uuid() + props.bot.id,
  //     },
  //     {
  //       onMessage(message) {
  //         console.warn(message);
  //       },
  //       onError(err) {
  //         console.error(err);
  //       },
  //       onFinished() {
  //         console.warn('success');
  //       },
  //     },
  //   );
  // }
});

onUnmounted(() => {
  console.log('unmounted');
});

function openCloseHeader() {
  if (showHeaderFlog.value) {
    senderRef.value.closeHeader();
  } else {
    senderRef.value.openHeader();
  }
  showHeaderFlog.value = !showHeaderFlog.value;
}

function closeHeader() {
  showHeaderFlog.value = false;
  senderRef.value.closeHeader();
}
</script>

<template>
  <div class="mx-auto h-full max-w-[780px]">
    <div
      :class="
        cn(
          'flex h-full w-full flex-col gap-2',
          !props.sessionId && 'items-center justify-center gap-8',
        )
      "
    >
      <!-- 对话列表 -->
      <div v-show="props.sessionId" class="flex-1 overflow-hidden">
        <BubbleList :list="bubbleItems" max-height="none" class="!h-full">
          <!-- 自定义头像 -->
          <template #avatar="{ item }">
            <BotAvatar
              v-if="item.role === 'assistant'"
              :src="bot?.icon"
              :size="40"
            />
            <ElAvatar v-else :src="userStore.userInfo?.avatar" :size="40" />
          </template>

          <!-- 自定义头部 -->
          <template #header="{ item }">
            <div class="header-wrapper">
              <div class="header-name">
                {{
                  item.role === 'assistant'
                    ? bot?.title
                    : userStore.userInfo?.nickname
                }}
              </div>
            </div>
          </template>

          <!-- 自定义气泡内容 -->
          <template #content="{ item }">
            <div class="content-wrapper">
              <div class="content-text">
                {{ item.content }}
              </div>
            </div>
          </template>

          <!-- 自定义底部 -->
          <template #footer="{ item }">
            <div class="footer-wrapper">
              <div class="footer-container">
                <ElButton
                  v-if="item.role === 'assistant'"
                  type="info"
                  :icon="Refresh"
                  size="small"
                  circle
                />
                <ElButton
                  color="#626aef"
                  :icon="DocumentCopy"
                  size="small"
                  circle
                />
              </div>
              <div class="footer-time">
                {{ new Date(item.created).toLocaleString() }}
              </div>
            </div>
          </template>

          <!-- 自定义 loading -->
          <template #loading>
            <div class="loading-container">
              <span>AI</span>
              <span>正</span>
              <span>在</span>
              <span>思</span>
              <span>考</span>
              <span>中</span>
              <span>...</span>
            </div>
          </template>
        </BubbleList>
      </div>

      <!-- 新对话显示bot信息 -->
      <div v-show="!props.sessionId" class="flex flex-col items-center gap-3.5">
        <BotAvatar :src="props.bot?.icon" :size="88" />
        <h1 class="text-base font-medium text-black/85">
          {{ props.bot?.title }}
        </h1>
        <span class="text-sm text-[#757575]">{{ props.bot?.description }}</span>
      </div>

      <!-- Sender -->
      <div class="flex w-full flex-col gap-3">
        <ElButton style="width: fit-content" @click="openCloseHeader">
          {{ showHeaderFlog ? '关闭头部' : '打开头部' }}
        </ElButton>
        <Sender ref="senderRef" v-model="senderValue">
          <template #header>
            <div class="header-self-wrap">
              <div class="header-self-title">
                <div class="header-left">💯 欢迎使用 Element Plus X</div>
                <div class="header-right">
                  <ElButton @click.stop="closeHeader">
                    <ElIcon><CircleClose /></ElIcon>
                    <span>关闭头部</span>
                  </ElButton>
                </div>
              </div>
              <div class="header-self-content">🦜 自定义头部内容</div>
            </div>
          </template>
        </Sender>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
.avatar-wrapper {
  width: 40px;
  height: 40px;
  img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }
}

.header-wrapper {
  .header-name {
    font-size: 14px;
    color: #979797;
  }
}

.content-wrapper {
  .content-text {
    font-size: 14px;
    color: #333;
    padding: 12px;
    background: linear-gradient(to right, #fdfcfb 0%, #ffd1ab 100%);
    border-radius: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
}

.footer-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  .footer-time {
    font-size: 12px;
    margin-top: 3px;
  }
}

.footer-container {
  :deep(.el-button + .el-button) {
    margin-left: 8px;
  }
}

.loading-container {
  font-size: 14px;
  color: #333;
  padding: 12px;
  background: linear-gradient(to right, #fdfcfb 0%, #ffd1ab 100%);
  border-radius: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading-container span {
  display: inline-block;
  margin-left: 8px;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(5px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.loading-container span:nth-child(4n) {
  animation: bounce 1.2s ease infinite;
}
.loading-container span:nth-child(4n + 1) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.3s;
}
.loading-container span:nth-child(4n + 2) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.6s;
}
.loading-container span:nth-child(4n + 3) {
  animation: bounce 1.2s ease infinite;
  animation-delay: 0.9s;
}

.header-self-wrap {
  display: flex;
  flex-direction: column;
  padding: 16px;
  height: 200px;
  .header-self-title {
    width: 100%;
    display: flex;
    height: 30px;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 8px;
  }
  .header-self-content {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: #626aef;
    font-weight: 600;
  }
}
</style>
