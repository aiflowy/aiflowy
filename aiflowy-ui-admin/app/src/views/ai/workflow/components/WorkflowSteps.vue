<script setup lang="ts">
import type { ServerSentEventMessage } from 'fetch-event-stream';

import { computed, ref, watch } from 'vue';

import {
  CircleCloseFilled,
  SuccessFilled,
  WarningFilled,
} from '@element-plus/icons-vue';
import { ElCollapse, ElCollapseItem, ElIcon } from 'element-plus';

import ShowJson from '#/components/json/ShowJson.vue';

export interface WorkflowStepsProps {
  workflowId: any;
  executeMessage: null | ServerSentEventMessage;
  nodeJson: any;
  initSignal?: boolean;
}
const props = defineProps<WorkflowStepsProps>();

const nodes = ref<any[]>([]);
const nodeStatusMap = ref<Record<string, any>>({});
const isChainError = ref(false);
watch(
  () => props.initSignal,
  () => {
    nodeStatusMap.value = {};
  },
);
watch(
  () => props.nodeJson,
  (newVal) => {
    if (newVal) {
      nodes.value = [...newVal];
    }
  },
  { immediate: true },
);
watch(
  () => props.executeMessage,
  (newMsg) => {
    if (newMsg && newMsg.data) {
      try {
        const msg = JSON.parse(newMsg.data).content;
        if (msg.status === 'error') {
          isChainError.value = true;
        }
        if (msg.nodeId && msg.status) {
          nodeStatusMap.value[msg.nodeId] = {
            status: msg.status,
            content: msg.res || msg.errorMsg,
          };
        }
      } catch (error) {
        console.error('parse sse message error:', error);
      }
    }
  },
  { deep: true },
);
const displayNodes = computed(() => {
  return nodes.value.map((node) => ({
    ...node,
    ...nodeStatusMap.value[node.key],
  }));
});
const activeName = ref('1');
</script>

<template>
  <div>
    <ElCollapse v-model="activeName" accordion expand-icon-position="left">
      <ElCollapseItem
        v-for="node in displayNodes"
        :key="node.key"
        :title="`${node.label}-${node.status}`"
        :name="node.key"
      >
        <template #title>
          <div class="flex items-center justify-between">
            <div>
              {{ node.label }}
            </div>
            <div class="flex items-center">
              <ElIcon v-if="node.status === 'end'" color="green" size="20">
                <SuccessFilled />
              </ElIcon>
              <div v-if="node.status === 'start'" class="spinner"></div>
              <ElIcon v-if="node.status === 'nodeError'" color="red" size="20">
                <CircleCloseFilled />
              </ElIcon>
              <ElIcon v-if="isChainError" color="orange" size="20">
                <WarningFilled />
              </ElIcon>
            </div>
          </div>
        </template>
        <div>
          <ShowJson :value="node.content" />
        </div>
      </ElCollapseItem>
    </ElCollapse>
  </div>
</template>

<style scoped>
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: var(--el-color-primary);
  border-right-color: var(--el-color-primary);
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
