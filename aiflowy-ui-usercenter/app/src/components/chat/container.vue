<script setup lang="ts">
import { nextTick, provide, ref, watch } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { Delete, Edit, MoreFilled } from '@element-plus/icons-vue';
import {
  ElAside,
  ElButton,
  ElContainer,
  ElDialog,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElHeader,
  ElInput,
  ElMain,
  ElMessage,
  ElMessageBox,
} from 'element-plus';

import { api } from '#/api/request';
import ChatIcon from '#/components/icons/ChatIcon.vue';
import { $t } from '#/locales';

interface Props {
  bot: any;
  isFold: boolean;
  onMessageList?: (list: any[]) => void;
  toggleFold: () => void;
}
const props = defineProps<Props>();
const sessionList = ref<any>([]);
const currentSession = ref<any>({});
const hoverId = ref<string>();
const dialogVisible = ref(false);
watch(
  () => props.bot.id,
  () => {
    getSessionList(true);
  },
);
defineExpose({
  getSessionList,
});

function getSessionList(resetSession = false) {
  api
    .get('/userCenter/botConversation/list', {
      params: {
        botId: props.bot.id,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        sessionList.value = res.data;
        if (resetSession) {
          currentSession.value = {};
        }
      }
    });
}
provide('getSessionList', getSessionList);
function addSession() {
  const newSession = sessionList.value.find(
    (session: any) => session.title === '新对话' && !session.created,
  );

  if (newSession) {
    return;
  }

  api.get('/userCenter/bot/generateConversationId').then((res) => {
    const data = {
      botId: props.bot.id,
      title: '新对话',
      id: res.data,
    };
    sessionList.value.unshift(data);

    nextTick(() => {
      clickSession(data);
    });
  });
}
function clickSession(session: any) {
  currentSession.value = session;
  getMessageList();
}
function getMessageList() {
  api
    .get('/userCenter/botMessage/getMessages', {
      params: {
        botId: props.bot.id,
        conversationId: currentSession.value.id,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        props.onMessageList?.(res.data);
      }
    });
}
function formatCreatedTime(time: string) {
  if (time) {
    const createTime = Math.floor(new Date(time).getTime() / 1000);
    const today = Math.floor(Date.now() / 1000 / 86_400) * 86_400;
    return time.split(' ')[createTime < today ? 0 : 1];
  }
  return '';
}
const handleMouseEvent = (id?: string) => {
  if (id === undefined) {
    setTimeout(() => {
      hoverId.value = id;
    }, 200);
  } else {
    hoverId.value = id;
  }
};
const updateLoading = ref(false);
function updateTitle() {
  updateLoading.value = true;
  api
    .get('/userCenter/botConversation/updateConversation', {
      params: {
        botId: props.bot.id,
        conversationId: currentSession.value.id,
        title: currentSession.value.title,
      },
    })
    .then((res) => {
      updateLoading.value = false;
      if (res.errorCode === 0) {
        dialogVisible.value = false;
        ElMessage.success('成功');
        getSessionList();
      }
    });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .get('/userCenter/botConversation/deleteConversation', {
            params: {
              botId: props.bot.id,
              conversationId: row.id,
            },
          })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              props.onMessageList?.([]);
              currentSession.value = {};
              ElMessage.success(res.message);
              done();
              getSessionList();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
</script>

<template>
  <ElContainer class="border-border bg-background h-full rounded-lg border">
    <ElAside width="287px" class="border-border border-r p-6">
      <!-- <Card class="max-w-max p-0">
        <CardAvatar :src="bot.icon" :default-avatar="defaultAssistantAvatar" />
        <CardContent>
          <CardTitle>{{ bot.title }}</CardTitle>
          <CardDescription>{{ bot.description }}</CardDescription>
        </CardContent>
      </Card> -->
      <span>会话</span>
      <ElButton
        class="mt-6 !h-10 w-full !text-sm"
        type="primary"
        :icon="ChatIcon"
        plain
        @click="addSession"
      >
        新建会话
      </ElButton>
      <div class="mt-8">
        <div
          v-for="conversation in sessionList"
          :key="conversation.id"
          :class="
            cn(
              'group flex h-10 cursor-pointer items-center justify-between gap-1 rounded-lg px-5 text-sm',
              currentSession.id === conversation.id
                ? 'bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                : 'hover:bg-[hsl(var(--accent))]',
            )
          "
          @click="clickSession(conversation)"
        >
          <span
            :class="
              cn(
                'text-foreground overflow-hidden text-ellipsis text-nowrap',
                currentSession.id === conversation.id && 'text-primary',
              )
            "
            :title="conversation.title || '未命名'"
          >
            {{ conversation.title || '未命名' }}
          </span>
          <span
            :class="
              cn(
                'text-foreground/50 text-nowrap text-xs group-hover:hidden',
                hoverId === conversation.id && 'hidden',
              )
            "
          >
            {{ formatCreatedTime(conversation.created) }}
          </span>
          <ElDropdown
            :class="
              cn(
                'group-hover:!inline-flex',
                (!hoverId || conversation.id !== hoverId) && '!hidden',
              )
            "
            @click.stop
            trigger="click"
          >
            <ElButton link :icon="MoreFilled" @click.stop />

            <template #dropdown>
              <ElDropdownMenu
                @mouseenter="handleMouseEvent(conversation.id)"
                @mouseleave="handleMouseEvent()"
              >
                <ElDropdownItem @click="dialogVisible = true">
                  <ElButton link :icon="Edit">编辑</ElButton>
                </ElDropdownItem>
                <ElDropdownItem>
                  <ElButton
                    @click="remove(conversation)"
                    link
                    type="danger"
                    :icon="Delete"
                  >
                    删除
                  </ElButton>
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>
      </div>
    </ElAside>
    <ElContainer>
      <ElHeader class="border-border border-b" height="53">
        <div class="flex items-center justify-between">
          <span class="text-base/[53px] font-medium">
            {{ currentSession.title || '未命名' }}
          </span>
          <IconifyIcon
            v-if="isFold"
            icon="svg:assistant-fold"
            class="rotate-180 cursor-pointer"
            @click="toggleFold"
          />
        </div>
      </ElHeader>
      <ElMain>
        <slot :conversation-id="currentSession.id"></slot>
      </ElMain>
    </ElContainer>
    <ElDialog title="编辑" v-model="dialogVisible">
      <div class="p-5">
        <ElForm>
          <ElFormItem>
            <ElInput
              v-model="currentSession.title"
              placeholder="请输入会话名称"
            />
          </ElFormItem>
        </ElForm>
      </div>

      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="updateTitle" :loading="updateLoading">
          确认
        </ElButton>
      </template>
    </ElDialog>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-button :deep(.el-icon) {
  font-size: 20px;
}
</style>
