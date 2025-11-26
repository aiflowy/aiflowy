<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { ArrowLeft, Position } from '@element-plus/icons-vue';
import { Tinyflow } from '@tinyflow-ai/vue';
import { ElButton } from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import { router } from '#/router';

import '@tinyflow-ai/vue/dist/index.css';

const route = useRoute();
// vue
onMounted(() => {
  document.addEventListener('keydown', handleKeydown);
  getWorkflowInfo(workflowId.value);
});
onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
});
// variables
const workflowId = ref(route.query.id);
const workflowData = ref<any>(null);
const showTinyFlow = ref(false);
const saveLoading = ref(false);
const handleKeydown = (event: KeyboardEvent) => {
  // 检查是否是 Ctrl+S
  if (event.ctrlKey && event.key === 's') {
    event.preventDefault(); // 阻止浏览器默认保存行为
    if (!saveLoading.value) {
      handleSave();
    }
  }
};
const workflowInfo = ref<any>({});
watch([() => workflowData.value], ([workflowData]) => {
  if (workflowData) {
    showTinyFlow.value = true;
  }
});
// functions
async function runWorkflow() {
  if (!saveLoading.value) {
    await handleSave();
    console.log('试运行');
  }
}
async function handleSave() {
  console.log('保存数据');
  saveLoading.value = true;
  // 模拟请求接口
  await new Promise((resolve: any) => setTimeout(resolve, 1000));
  saveLoading.value = false;
}
function getWorkflowInfo(workflowId: any) {
  api.get(`/api/v1/aiWorkflow/detail?id=${workflowId}`).then((res) => {
    workflowInfo.value = res.data;
    if (workflowInfo.value.content) {
      workflowData.value = JSON.parse(workflowInfo.value.content);
    }
  });
}
</script>

<template>
  <div class="head-div h-full w-full">
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
        <ElButton type="primary" :disabled="saveLoading" @click="handleSave">
          {{ $t('button.save') }}(ctrl+s)
        </ElButton>
      </div>
    </div>
    <Tinyflow
      v-if="showTinyFlow"
      class="tiny-flow-container"
      :data="workflowData"
    />
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
</style>
