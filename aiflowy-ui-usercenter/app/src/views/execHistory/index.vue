<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Delete, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElContainer,
  ElDatePicker,
  ElHeader,
  ElMain,
  ElSelect,
  ElSpace,
  ElTag,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

onMounted(() => {
  getWorkflowList();
});
const options = [
  {
    value: 1,
    label: $t('aiWorkflowExecRecord.status1'),
  },
  {
    value: 5,
    label: $t('aiWorkflowExecRecord.status5'),
  },
  {
    value: 10,
    label: $t('aiWorkflowExecRecord.status10'),
  },
  {
    value: 20,
    label: $t('aiWorkflowExecRecord.status20'),
  },
  {
    value: 21,
    label: $t('aiWorkflowExecRecord.status21'),
  },
  {
    value: 22,
    label: $t('aiWorkflowExecRecord.status22'),
  },
];

const listTitles = ['任务名称', '启动时间', '耗时', '状态', '操作'];

const queryParams = ref<any>({});
const pageRef = ref();
const workflowList = ref<any[]>([]);
const router = useRouter();
function search() {
  getDateRange();
  pageRef.value.setQuery(queryParams.value);
}
function getWorkflowList() {
  api
    .get('/userCenter/aiWorkflow/list', {
      params: { ...queryParams.value },
    })
    .then((res) => {
      workflowList.value = res.data;
    });
}
function getTagType(row: any) {
  switch (row.status) {
    case 1: {
      return 'primary';
    }
    case 5: {
      return 'warning';
    }
    case 10: {
      return 'danger';
    }
    case 20: {
      return 'success';
    }
    case 21: {
      return 'danger';
    }
    default: {
      return 'info';
    }
  }
}
function toDetail(record: any) {
  router.push({
    path: `/execHistory/${record.id}`,
    query: {
      title: record.title,
    },
  });
  localStorage.setItem(`${record.id}-workflow-json`, record.workflowJson);
}
const dateRange = ref<any>('');
function getDateRange() {
  if (dateRange.value) {
    queryParams.value.queryBegin = dateRange.value[0];
    queryParams.value.queryEnd = dateRange.value[1];
  } else {
    queryParams.value.queryBegin = '';
    queryParams.value.queryEnd = '';
  }
}
</script>

<template>
  <ElContainer class="h-full bg-[linear-gradient(153deg,white,#EFF8FF)]">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium text-[#333333]">执行记录</h1>
        <div class="flex items-center gap-8">
          <div class="flex items-center gap-4">
            <span class="text-nowrap text-sm text-[#1A1A1A]">执行状态</span>
            <ElSelect
              clearable
              v-model="queryParams.status"
              :options="options"
              placeholder="请选择执行状态"
              @change="search"
            />
          </div>
          <div class="flex items-center gap-4">
            <span class="text-nowrap text-sm text-[#1A1A1A]">智能体</span>
            <ElSelect
              class="bot-select"
              clearable
              :options="workflowList"
              placeholder="请选择智能体"
              v-model="queryParams.workflowId"
              @change="search"
              :props="{
                value: 'id',
                label: 'title',
              }"
            />
          </div>
          <div class="flex items-center gap-4">
            <span class="text-nowrap text-sm text-[#1A1A1A]">筛选时间</span>
            <ElDatePicker
              clearable
              type="daterange"
              v-model="dateRange"
              start-placeholder="选择开始日期"
              end-placeholder="选择结束日期"
              @change="search"
              value-format="YYYY-MM-DD"
            />
          </div>
        </div>
      </ElSpace>
    </ElHeader>
    <ElMain class="!px-8">
      <ElContainer>
        <ElHeader
          class="grid grid-cols-6 place-items-center bg-[#F9FAFE] !p-0"
          height="54px"
        >
          <span
            class="text-sm text-[#1A1A1A]"
            v-for="title in listTitles"
            :key="title"
          >
            {{ title }}
          </span>
        </ElHeader>
        <ElMain class="!p-0">
          <div class="flex flex-col items-center gap-5">
            <div class="w-full">
              <PageData
                page-url="/userCenter/aiWorkflowExecRecord/getPage"
                ref="pageRef"
              >
                <template #default="{ pageList }">
                  <div
                    class="grid h-[60px] grid-cols-6 place-items-center bg-white text-sm text-[#333333]"
                    v-for="record in pageList"
                    :key="record.id"
                  >
                    <span>{{ record.title }}</span>
                    <span>{{ record.startTime }}</span>
                    <span>{{ record.execTime }} ms</span>
                    <span>
                      <ElTag :type="getTagType(record)">
                        {{ $t(`aiWorkflowExecRecord.status${record.status}`) }}
                      </ElTag>
                    </span>
                    <ElSpace :size="10">
                      <ElButton
                        type="primary"
                        :icon="View"
                        link
                        @click="toDetail(record)"
                      >
                        查看详情
                      </ElButton>
                      <ElButton type="danger" :icon="Delete" link>
                        删除
                      </ElButton>
                    </ElSpace>
                  </div>
                </template>
              </PageData>
            </div>
          </div>
        </ElMain>
      </ElContainer>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-select {
  --el-select-width: 165px;
}

.el-select.bot-select {
  --el-select-width: 343px;
}

.el-select :deep(.el-select__wrapper) {
  --el-fill-color-blank: #f6f6f6;
  --el-border-radius-base: 8px;
}

:deep(.el-date-editor) {
  --el-input-bg-color: #f6f6f6;
}
</style>
