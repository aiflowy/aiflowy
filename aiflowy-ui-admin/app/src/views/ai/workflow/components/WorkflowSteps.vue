<script setup lang="ts">
import type { ServerSentEventMessage } from 'fetch-event-stream';

import { computed, reactive, ref, watch } from 'vue';

import { sortNodes } from '@aiflowy/utils';

import { ElCollapse, ElCollapseItem } from 'element-plus';

export interface WorkflowStepsProps {
  workflowId: any;
  executeMessage: null | ServerSentEventMessage;
  nodeJson: any;
}
const props = defineProps<WorkflowStepsProps>();
const nodeStates = reactive<Record<string, string>>({});
const nodeResults = reactive<Record<string, any>>({});
watch(
  () => props.executeMessage,
  (newMessage) => {
    if (newMessage && newMessage.data) {
      try {
        const parsedMessage = JSON.parse(newMessage.data).content;
        if (parsedMessage && parsedMessage.nodeId && parsedMessage.status) {
          nodeStates[parsedMessage.nodeId] = parsedMessage.status;
          if (parsedMessage.status === 'end') {
            nodeResults[parsedMessage.nodeId] = parsedMessage.res;
          }
        }
      } catch (error) {
        console.error('解析消息失败:', error);
      }
    }
  },
  { immediate: true },
);

const nodes = computed(() => {
  const sortedNodes = sortNodes(props.nodeJson);

  // 为每个节点设置初始状态
  sortedNodes.forEach((node) => {
    if (!nodeStates[node.key]) {
      nodeStates[node.key] = node.status || 'pending';
    }
  });

  return sortedNodes.map((node) => ({
    ...node,
    status: nodeStates[node.key] || node.status || 'pending',
    result: nodeResults[node.key] || '',
  }));
});
const activeName = ref('1');
</script>

<template>
  <div>
    <ElCollapse v-model="activeName" accordion expand-icon-position="left">
      <ElCollapseItem
        v-for="(node, idx) in nodes"
        :key="idx"
        :title="`${node.label}-${node.status}`"
        :name="node.key"
      >
        {{ node.key }} - {{ node.result }}
      </ElCollapseItem>
    </ElCollapse>
  </div>
</template>

<style scoped></style>
