<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router';

import { sortNodes } from '@aiflowy/utils';

import { ArrowLeft } from '@element-plus/icons-vue';
import {
  ElContainer,
  ElDivider,
  ElHeader,
  ElIcon,
  ElMain,
  ElSpace,
} from 'element-plus';

import { api } from '#/api/request';
import ExecResult from '#/views/ai/workflow/components/ExecResult.vue';
import WorkflowSteps from '#/views/ai/workflow/components/WorkflowSteps.vue';

const router = useRouter();
const route = useRoute();
const workflowJson = localStorage.getItem(`${route.params.id}-workflow-json`);
const nodeJson = JSON.parse(workflowJson || '{}');

onMounted(() => {
  getStepList();
  if (!workflowJson) {
    router.push({
      path: '/execHistory',
    });
  }
});
onBeforeRouteLeave(() => {
  localStorage.removeItem(`${route.params.id}-workflow-json`);
});

const stepList = ref<any>([]);

function getStepList() {
  api
    .get('/userCenter/aiWorkflowRecordStep/getListByRecordId', {
      params: {
        recordId: route.params.id,
      },
    })
    .then((res) => {
      stepList.value = res.data;
    });
}
const result = computed(() => {
  if (stepList.value.length > 0) {
    const finalNode = stepList.value[stepList.value.length - 1];
    return {
      status: finalNode.status,
      result: JSON.parse(finalNode.output),
      message: finalNode.errorInfo,
    };
  } else {
    return {};
  }
});
const steps = computed(() => {
  return stepList.value.length > 0
    ? stepList.value.map((item: any) => {
        return {
          key: item.id,
          label: item.nodeName,
          status: item.status,
          message: item.errorInfo,
          result: JSON.parse(item.output || '{}'),
          original: {
            type: '',
          },
        };
      })
    : [];
});
</script>

<template>
  <ElContainer class="h-full bg-white">
    <ElHeader class="!p-8 !pb-0" height="auto">
      <ElSpace class="cursor-pointer" :size="10" @click="router.back()">
        <ElIcon color="#969799" size="24"><ArrowLeft /></ElIcon>
        <h1 class="text-2xl font-medium text-[#333333]">
          {{ route.query.title }}
        </h1>
      </ElSpace>
    </ElHeader>
    <ElMain class="items-center !px-8">
      <div
        class="flex h-full overflow-hidden rounded-xl border border-[#E6E9EE]"
      >
        <div class="flex w-[50%] flex-col gap-6 rounded-lg bg-white p-5">
          <h1 class="text-base font-medium text-[#1A1A1A]">运行结果</h1>
          <div
            class="flex-1 rounded-lg border border-[#F0F0F0] bg-[#F7F7F7] p-4"
          >
            <ExecResult
              v-if="nodeJson"
              workflow-id="workflowId"
              :node-json="sortNodes(nodeJson)"
              :polling-data="result"
              :show-message="false"
            />
          </div>
        </div>
        <ElDivider
          class="!m-0 !h-full !border-[#E6E9EE]"
          direction="vertical"
        />
        <div class="flex h-full w-[49%] flex-col gap-6 rounded-lg bg-white p-5">
          <h1 class="text-base font-medium text-[#1A1A1A]">执行步骤</h1>
          <WorkflowSteps
            v-if="nodeJson"
            workflow-id="workflowId"
            :node-json="steps"
            :polling-data="result"
          />
        </div>
      </div>
    </ElMain>
  </ElContainer>
</template>
