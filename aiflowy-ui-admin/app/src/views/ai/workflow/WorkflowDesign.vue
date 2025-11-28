<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { getOptions, sortNodes } from '@aiflowy/utils';

import { ArrowLeft, Position } from '@element-plus/icons-vue';
import { Tinyflow } from '@tinyflow-ai/vue';
import { ElButton, ElDrawer, ElMessage, ElSkeleton } from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import { router } from '#/router';
import ExecResult from '#/views/ai/workflow/components/ExecResult.vue';
import WorkflowForm from '#/views/ai/workflow/components/WorkflowForm.vue';
import WorkflowSteps from '#/views/ai/workflow/components/WorkflowSteps.vue';

import { getCustomNode } from './customNode/index';

import '@tinyflow-ai/vue/dist/index.css';

const route = useRoute();
// vue
onMounted(async () => {
  document.addEventListener('keydown', handleKeydown);
  await Promise.all([
    loadCustomNode(),
    getLlmList(),
    getKnowledgeList(),
    getWorkflowInfo(workflowId.value),
  ]);
  showTinyFlow.value = true;
});
onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
});
// variables
const tinyflowRef = ref<InstanceType<typeof Tinyflow> | null>(null);
const workflowId = ref(route.query.id);
const workflowInfo = ref<any>({});
const runParams = ref<any>(null);
const tinyFlowData = ref<any>(null);
const llmList = ref<any>([]);
const knowledgeList = ref<any>([]);
const provider = computed(() => ({
  llm: () => getOptions('title', 'id', llmList.value),
  knowledge: () => getOptions('title', 'id', knowledgeList.value),
  searchEngine: (): any => [
    {
      value: 'bocha',
      label: '博查搜索',
    },
  ],
}));
const customNode = ref();
const showTinyFlow = ref(false);
const saveLoading = ref(false);
const handleKeydown = (event: KeyboardEvent) => {
  // 检查是否是 Ctrl+S
  if (event.ctrlKey && event.key === 's') {
    event.preventDefault(); // 阻止浏览器默认保存行为
    if (!saveLoading.value) {
      handleSave(true);
    }
  }
};
const drawerVisible = ref(false);
const executeMessage = ref<any>(null);
const initState = ref(false);
// functions
async function loadCustomNode() {
  customNode.value = await getCustomNode({
    handleChosen: (nodeType: string, updateNodeData: any, value: string) => {
      console.log('nodeType:', nodeType);
      console.log('updateNodeData:', updateNodeData);
      console.log('value:', value);
    },
  });
}
async function runWorkflow() {
  if (!saveLoading.value) {
    await handleSave().then(() => {
      getWorkflowInfo(workflowId.value);
      getRunningParams();
    });
  }
}
async function handleSave(showMsg: boolean = false) {
  saveLoading.value = true;
  await api
    .post('/api/v1/aiWorkflow/update', {
      id: workflowId.value,
      content: tinyflowRef.value?.getData(),
    })
    .then((res) => {
      saveLoading.value = false;
      if (res.errorCode === 0 && showMsg) {
        ElMessage.success(res.message);
      }
    });
}
async function getWorkflowInfo(workflowId: any) {
  api.get(`/api/v1/aiWorkflow/detail?id=${workflowId}`).then((res) => {
    workflowInfo.value = res.data;
    tinyFlowData.value = workflowInfo.value.content
      ? JSON.parse(workflowInfo.value.content)
      : {};
  });
}
async function getLlmList() {
  api.get('/api/v1/aiLlm/list').then((res) => {
    llmList.value = res.data;
  });
}
async function getKnowledgeList() {
  api.get('/api/v1/aiKnowledge/list').then((res) => {
    knowledgeList.value = res.data;
  });
}
function getRunningParams() {
  api
    .get(`/api/v1/aiWorkflow/getRunningParameters?id=${workflowId.value}`)
    .then((res) => {
      runParams.value = res.data;
      drawerVisible.value = true;
    });
}
function onExecuting(msg: any) {
  executeMessage.value = msg;
}
function onSubmit() {
  initState.value = !initState.value;
}
async function runIndependently(node: any) {
  if (node.type === 'loopNode') {
    ElMessage.warning($t('message.notSupported'));
    return;
  }
  console.warn('node:', node);
}
</script>

<template>
  <div class="head-div h-full w-full">
    <ElDrawer v-model="drawerVisible" :title="$t('button.run')" size="600px">
      <div class="mb-2.5 font-semibold">{{ $t('aiWorkflow.params') }}：</div>
      <WorkflowForm
        :workflow-id="workflowId"
        :workflow-params="runParams"
        :on-executing="onExecuting"
        :on-submit="onSubmit"
      />
      <div class="mb-2.5 font-semibold">{{ $t('aiWorkflow.steps') }}：</div>
      <WorkflowSteps
        :workflow-id="workflowId"
        :execute-message="executeMessage"
        :node-json="sortNodes(tinyFlowData)"
        :init-signal="initState"
      />
      <div class="mb-2.5 mt-2.5 font-semibold">
        {{ $t('aiWorkflow.result') }}：
      </div>
      <ExecResult
        :workflow-id="workflowId"
        :execute-message="executeMessage"
        :node-json="sortNodes(tinyFlowData)"
        :init-signal="initState"
      />
    </ElDrawer>
    <div class="flex items-center justify-between border-b p-2.5">
      <div>
        <ElButton :icon="ArrowLeft" link @click="router.back()">
          {{ workflowInfo.title }}
        </ElButton>
      </div>
      <div>
        <ElButton :disabled="saveLoading" :icon="Position" @click="runWorkflow">
          {{ $t('button.runTest') }}
        </ElButton>
        <ElButton
          type="primary"
          :disabled="saveLoading"
          @click="handleSave(true)"
        >
          {{ $t('button.save') }}(ctrl+s)
        </ElButton>
      </div>
    </div>
    <Tinyflow
      ref="tinyflowRef"
      v-if="showTinyFlow"
      class="tiny-flow-container"
      :data="JSON.parse(JSON.stringify(tinyFlowData))"
      :provider="provider"
      :custom-nodes="customNode"
      :on-node-execute="runIndependently"
    />
    <ElSkeleton class="load-div" v-else :rows="5" animated />
  </div>
</template>

<style scoped>
:deep(.tf-toolbar-container-body) {
  height: calc(100vh - 365px) !important;
  overflow-y: auto;
}
:deep(.agentsflow) {
  height: calc(100vh - 130px) !important;
}
.head-div {
  background-color: var(--el-bg-color);
}
.tiny-flow-container {
  height: calc(100vh - 150px);
  width: 100%;
}
.load-div {
  margin: 20px;
}
</style>
