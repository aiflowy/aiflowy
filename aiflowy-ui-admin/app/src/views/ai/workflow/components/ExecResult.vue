<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { preferences } from '@aiflowy/preferences';

import { ElEmpty, ElMessage, ElRow } from 'element-plus';

import ShowJson from '#/components/json/ShowJson.vue';
import { $t } from '#/locales';
import ExecResultItem from '#/views/ai/workflow/components/ExecResultItem.vue';

export interface ExecResultProps {
  workflowId: any;
  nodeJson: any;
  initSignal?: boolean;
  pollingData?: any;
}
const props = defineProps<ExecResultProps>();

const finalNode = computed(() => {
  const nodes = props.nodeJson;
  if (nodes.length > 0) {
    let endNode = nodes[nodes.length - 1].original;
    for (const node of nodes) {
      if (node.original.type === 'endNode') {
        endNode = node.original;
      }
    }
    return endNode;
  }
  return {};
});
const result = ref('');
const success = ref(false);
watch(
  () => props.initSignal,
  () => {
    result.value = '';
  },
);
watch(
  () => props.pollingData,
  (newVal) => {
    if (newVal.status === 20) {
      ElMessage.success($t('message.success'));
      result.value = newVal.result;
      success.value = true;
    }
    if (newVal.status === 21) {
      ElMessage.error($t('message.fail'));
      result.value = newVal.message;
      success.value = false;
    }
  },
  { deep: true },
);
function getResultCount(res: any[]) {
  if (res.length > 1 || finalNode.value.data.outputDefs.length > 1) {
    return 2;
  }
  return 1;
}
function getResult(res: any) {
  return Array.isArray(res) ? res : [res];
}
</script>

<template>
  <div>
    <div v-if="finalNode.type === 'endNode' && success">
      <ElRow :gutter="12" v-if="finalNode.data.outputDefs && result">
        <template
          v-for="outputDef in finalNode.data.outputDefs"
          :key="outputDef.id"
        >
          <ExecResultItem
            :result="getResult(result[outputDef.name])"
            :result-count="getResultCount(getResult(result[outputDef.name]))"
            :content-type="outputDef.contentType || 'text'"
            :def-name="outputDef.name"
          />
        </template>
      </ElRow>
    </div>
    <div v-if="finalNode.type !== 'endNode' && !success">
      <ShowJson :value="result" />
    </div>
    <div>
      <ElEmpty
        :image="`/empty${preferences.theme.mode === 'dark' ? '-dark' : ''}.png`"
        v-if="!result"
      />
    </div>
  </div>
</template>

<style scoped></style>
