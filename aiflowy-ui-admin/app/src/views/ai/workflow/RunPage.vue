<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { sortNodes } from '@aiflowy/utils';

import { ElAvatar, ElCard, ElCol, ElRow } from 'element-plus';

import { api } from '#/api/request';
import workflowIcon from '#/assets/ai/workflow/workflowIcon.png';
import { $t } from '#/locales';
import ExecResult from '#/views/ai/workflow/components/ExecResult.vue';
import WorkflowForm from '#/views/ai/workflow/components/WorkflowForm.vue';
import WorkflowSteps from '#/views/ai/workflow/components/WorkflowSteps.vue';

onMounted(async () => {
  pageLoading.value = true;
  await Promise.all([getWorkflowInfo(workflowId.value), getRunningParams()]);
  pageLoading.value = false;
});
const pageLoading = ref(false);
const route = useRoute();
const workflowId = ref(route.query.id);
const workflowInfo = ref<any>({});
const runParams = ref<any>(null);
const initState = ref(false);
const tinyFlowData = ref<any>(null);
const workflowForm = ref();
async function getWorkflowInfo(workflowId: any) {
  api.get(`/api/v1/aiWorkflow/detail?id=${workflowId}`).then((res) => {
    workflowInfo.value = res.data;
    tinyFlowData.value = workflowInfo.value.content
      ? JSON.parse(workflowInfo.value.content)
      : {};
  });
}
async function getRunningParams() {
  api
    .get(`/api/v1/aiWorkflow/getRunningParameters?id=${workflowId.value}`)
    .then((res) => {
      runParams.value = res.data;
    });
}
function onSubmit() {
  initState.value = !initState.value;
}
function resumeChain(data: any) {
  workflowForm.value?.resume(data);
}
const chainInfo = ref<any>(null);
function onAsyncExecute(info: any) {
  chainInfo.value = info;
}
</script>

<template>
  <div
    v-loading="pageLoading"
    class="bg-background-deep flex h-full w-full flex-col gap-6 overflow-hidden p-6"
  >
    <div
      class="flex h-[150px] shrink-0 items-center gap-6 rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] pl-11"
    >
      <ElAvatar :src="workflowInfo.icon ?? workflowIcon" :size="72" />
      <div class="flex flex-col gap-5">
        <span class="text-3xl font-medium">{{ workflowInfo.title }}</span>
        <span class="text-base">{{ workflowInfo.description }}</span>
      </div>
    </div>
    <ElRow class="flex-1" :gutter="10">
      <ElCol :span="10">
        <div class="flex h-full flex-col gap-2.5">
          <ElCard
            shadow="never"
            class="flex-1"
            body-class="overflow-auto h-full"
          >
            <div class="mb-2.5 font-semibold">
              {{ $t('aiWorkflow.params') }}：
            </div>
            <WorkflowForm
              v-if="runParams && tinyFlowData"
              ref="workflowForm"
              :workflow-id="workflowId"
              :workflow-params="runParams"
              :on-submit="onSubmit"
              :on-async-execute="onAsyncExecute"
              :tiny-flow-data="tinyFlowData"
            />
          </ElCard>
          <ElCard
            shadow="never"
            class="flex-1"
            body-class="overflow-auto h-full"
          >
            <div class="mb-2.5 font-semibold">
              {{ $t('aiWorkflow.steps') }}：
            </div>
            <WorkflowSteps
              v-if="tinyFlowData"
              :workflow-id="workflowId"
              :node-json="sortNodes(tinyFlowData)"
              :init-signal="initState"
              :polling-data="chainInfo"
              @resume="resumeChain"
            />
          </ElCard>
        </div>
      </ElCol>
      <ElCol :span="14">
        <ElCard shadow="never" class="h-full" body-class="h-full overflow-auto">
          <div class="mb-2.5 mt-2.5 font-semibold">
            {{ $t('aiWorkflow.result') }}：
          </div>
          <ExecResult
            v-if="tinyFlowData"
            :workflow-id="workflowId"
            :node-json="sortNodes(tinyFlowData)"
            :init-signal="initState"
            :polling-data="chainInfo"
          />
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>
