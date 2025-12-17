<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { Search } from '@element-plus/icons-vue';
import { ElContainer, ElHeader, ElInput, ElMain, ElSpace } from 'element-plus';

import { api } from '#/api/request';
import defaultBotAvatar from '#/assets/defaultBotAvatar.png';
import {
  Card,
  CardAvatar,
  CardContent,
  CardDescription,
  CardTitle,
} from '#/components/card';

const categories = ref<any[]>([]);
const workflowList = ref<any[]>([]);
const queryParams = ref<any>({});
const pageLoading = ref(false);
const activeTag = ref('');
onMounted(async () => {
  getWorkflowList();
  getCategoryList();
});
function getCategoryList() {
  api.get('/userCenter/aiWorkflowCategory/list').then((res) => {
    categories.value = [
      {
        id: '',
        categoryName: '全部',
      },
      ...res.data,
    ];
  });
}
function getWorkflowList() {
  pageLoading.value = true;
  api
    .get('/userCenter/aiWorkflow/list', {
      params: { ...queryParams.value },
    })
    .then((res) => {
      pageLoading.value = false;
      workflowList.value = res.data;
    });
}
function handleTagClick(tag: any) {
  activeTag.value = tag;
  queryParams.value.categoryId = tag;
  getWorkflowList();
}
</script>

<template>
  <ElContainer class="h-full bg-[linear-gradient(153deg,white,#EFF8FF)]">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium text-[#333333]">智能体</h1>
        <ElSpace :size="20">
          <ElInput
            placeholder="搜索"
            v-model="queryParams.title"
            @keyup.enter="getWorkflowList"
            :prefix-icon="Search"
          />
          <ElSpace :size="12">
            <button
              type="button"
              :class="
                activeTag === category.id
                  ? 'border-[#0066FF] bg-[rgba(0,102,255,0.08)]'
                  : ''
              "
              class="h-[35px] w-[94px] rounded-3xl border border-[#E6E9EE] text-sm text-[#566882] hover:border-[#0066FF] hover:bg-[rgba(0,102,255,0.08)] hover:text-[#0066FF]"
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
      <div class="flex flex-wrap items-center gap-5" v-loading="pageLoading">
        <RouterLink
          v-for="workflow in workflowList"
          :key="workflow.id"
          :to="`/workflow/${workflow.id}`"
          class="w-full max-w-[378px]"
        >
          <Card
            class="h-[168px] w-full max-w-[378px] flex-col gap-3 rounded-xl border border-[#E6E9EE] p-6 pb-0 pr-4 transition hover:-translate-y-2 hover:shadow-xl"
          >
            <CardContent class="flex-row items-center gap-3">
              <CardAvatar
                :src="workflow.icon"
                :default-avatar="defaultBotAvatar"
              />
              <CardTitle class="text-[#042A62]">{{ workflow.title }}</CardTitle>
            </CardContent>
            <CardDescription
              class="line-clamp-2 text-sm text-[#566882]"
              :title="workflow.description"
            >
              {{ workflow.description }}
            </CardDescription>
          </Card>
        </RouterLink>
      </div>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-input :deep(.el-input__wrapper) {
  --el-input-border-radius: 18px;
  --el-input-border-color: #e6e9ee;
}
</style>
