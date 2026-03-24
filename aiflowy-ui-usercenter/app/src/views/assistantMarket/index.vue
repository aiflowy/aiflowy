<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { IconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { Minus, Plus, Search } from '@element-plus/icons-vue';
import {
  ElButton,
  ElContainer,
  ElHeader,
  ElInput,
  ElMain,
  ElMessage,
  ElSpace,
} from 'element-plus';

import { api } from '#/api/request';
import defaultBotAvatar from '#/assets/defaultBotAvatar.png';
import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';
import { $t } from '#/locales';

const router = useRouter();
const categories = ref<any[]>([]);
const botList = ref<any[]>([]);
const queryParams = ref<any>({});
const pageLoading = ref(false);
const activeTag = ref('');
const usedList = ref<any[]>([]);
const btnLoading = ref(false);
onMounted(async () => {
  getBotList();
  getCategoryList();
  getUserUsed();
});
function getCategoryList() {
  api.get('/userCenter/botCategory/list').then((res) => {
    categories.value = [
      {
        id: '',
        categoryName: '全部',
      },
      ...res.data,
    ];
  });
}
function getBotList() {
  pageLoading.value = true;
  api
    .get('/userCenter/bot/list', {
      params: { ...queryParams.value, status: 1 },
    })
    .then((res) => {
      pageLoading.value = false;
      botList.value = res.data;
    });
}
function handleTagClick(tag: any) {
  activeTag.value = tag;
  queryParams.value.categoryId = tag;
  getBotList();
}
function getUserUsed() {
  api.get('/userCenter/botRecentlyUsed/list').then((res) => {
    usedList.value = res.data.map((item: any) => item.botId);
  });
}
function addBotToRecentlyUsed(botId: any) {
  btnLoading.value = true;
  api
    .post('/userCenter/botRecentlyUsed/save', {
      botId,
    })
    .then((res) => {
      btnLoading.value = false;
      if (res.errorCode === 0) {
        ElMessage.success($t('message.success'));
        getUserUsed();
        getBotList();
      }
    });
}
function removeBotFromRecentlyUsed(botId: any) {
  btnLoading.value = true;
  api
    .get('/userCenter/botRecentlyUsed/removeByBotId', {
      params: {
        botId,
      },
    })
    .then((res) => {
      btnLoading.value = false;
      if (res.errorCode === 0) {
        ElMessage.success($t('message.success'));
        getUserUsed();
        getBotList();
      }
    });
}
</script>

<template>
  <ElContainer class="bg-background-deep h-full">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium">智能体中心</h1>
        <ElSpace :size="20">
          <ElInput
            placeholder="搜索"
            v-model="queryParams.title"
            @keyup.enter="getBotList"
            :prefix-icon="Search"
          />
          <ElSpace :size="12">
            <button
              type="button"
              :class="
                cn(
                  'border-border text-foreground bg-background h-[35px] w-[94px] rounded-3xl border text-sm',
                  activeTag === category.id
                    ? 'border-primary text-primary bg-primary/10'
                    : 'hover:bg-accent',
                )
              "
              v-for="category in categories"
              :key="category.id"
              @click="handleTagClick(category.id)"
            >
              {{ category.categoryName }}
            </button>
          </ElSpace>
        </ElSpace>
      </ElSpace>
    </ElHeader>
    <ElMain class="!px-8">
      <div
        class="grid grid-cols-[repeat(auto-fill,minmax(300px,1fr))] gap-5"
        v-loading="pageLoading"
      >
        <Card
          class="border-border bg-background h-[168px] w-full max-w-none flex-col justify-between rounded-xl border p-6 pb-5 transition hover:-translate-y-2 hover:shadow-[0px_2px_16px_0px_rgba(6,27,57,0.07)]"
          v-for="assistant in botList"
          :key="assistant.id"
        >
          <CardContent class="gap-3">
            <CardContent class="flex-row items-center gap-3">
              <CardAvatar
                :src="assistant.icon"
                :default-avatar="defaultBotAvatar"
              />
              <CardTitle :title="assistant.title">
                {{ assistant.title }}
              </CardTitle>
            </CardContent>
            <CardDescription
              class="text-foreground/50 line-clamp-2 text-wrap text-sm"
              :title="assistant.description"
            >
              {{ assistant.description }}
            </CardDescription>
          </CardContent>
          <div class="flex w-full items-center">
            <ElButton
              v-if="!usedList.includes(assistant.id)"
              :loading="btnLoading"
              class="w-full !border-none"
              type="primary"
              :icon="Plus"
              plain
              @click="addBotToRecentlyUsed(assistant.id)"
            >
              添加到我的智能体
            </ElButton>
            <ElButton
              v-else
              :loading="btnLoading"
              class="w-full !border-none"
              type="primary"
              :icon="Minus"
              plain
              @click="removeBotFromRecentlyUsed(assistant.id)"
            >
              从我的智能体移除
            </ElButton>

            <ElButton
              class="w-full !border-none"
              type="primary"
              plain
              @click="router.push(`/assistantMarket/${assistant.id}`)"
            >
              <template #icon>
                <IconifyIcon icon="mdi:play-outline" />
              </template>
              立即体验
            </ElButton>
          </div>

          <!-- <ElRow class="w-full" :gutter="16">
            <ElCol :span="12">

            </ElCol>
            <ElCol :span="12">

            </ElCol>
          </ElRow> -->
        </Card>
      </div>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-input :deep(.el-input__wrapper) {
  --el-input-border-radius: 18px;
  --el-input-border-color: #e6e9ee;
}

:deep(.el-button) {
  --el-font-size-base: 12px;
  --el-button-font-weight: 400;
}
</style>
