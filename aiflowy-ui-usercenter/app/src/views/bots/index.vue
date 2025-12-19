<script setup lang="ts">
import { onMounted, ref } from 'vue';

import { Search } from '@element-plus/icons-vue';
import { ElContainer, ElHeader, ElInput, ElMain, ElSpace } from 'element-plus';

import { api } from '#/api/request';
import defaultBotAvatar from '#/assets/ai/workflow/workflowIcon.png';
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
      params: { ...queryParams.value, status: 1 },
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
  <ElContainer class="bg-background-deep h-full">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium">智能体</h1>
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
                  ? 'border-primary text-primary bg-[hsl(var(--primary)/15%)] dark:bg-[hsl(var(--accent))]'
                  : 'hover:bg-[hsl(var(--accent))]'
              "
              class="border-border text-foreground h-[35px] w-[94px] rounded-3xl border text-sm"
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
        <RouterLink
          v-for="workflow in workflowList"
          :key="workflow.id"
          :to="`/workflow/${workflow.id}`"
        >
          <Card
            class="border-border bg-background h-[168px] max-w-none flex-col gap-3 rounded-xl border p-6 pb-0 pr-4 transition hover:-translate-y-2 hover:shadow-md"
          >
            <CardContent class="flex-row items-center gap-3">
              <CardAvatar
                :src="workflow.icon"
                :default-avatar="defaultBotAvatar"
              />
              <CardTitle>{{ workflow.title }}</CardTitle>
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
