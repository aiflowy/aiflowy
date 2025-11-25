<script setup lang="ts">
import { $t } from '@aiflowy/locales';

import { Back } from '@element-plus/icons-vue';
import { ElButton, ElStep, ElSteps } from 'element-plus';

const emits = defineEmits(['importBack']);
const back = () => {
  emits('importBack');
};
</script>

<template>
  <div class="imp-doc-container">
    <div class="imp-doc-header">
      <ElButton @click="back" :icon="Back">
        {{ $t('button.back') }}
      </ElButton>
    </div>
    <div class="imp-doc-content">
      <ElSteps :active="1">
        <ElStep :title="$t('aiKnowledge.importDoc.fileUpload')" />
        <ElStep :title="$t('aiKnowledge.importDoc.parameterSettings')" />
        <ElStep :title="$t('aiKnowledge.importDoc.segmentedPreview')" />
        <ElStep :title="$t('aiKnowledge.importDoc.confirmImport')" />
      </ElSteps>
    </div>
  </div>
</template>

<style scoped>
.imp-doc-container {
  height: 100%;
  background-color: var(--el-color-white);
  border-radius: 12px;
  padding: 20px;
  display: block;
}

.imp-doc-content {
  height: 100%;
  padding-top: 20px;
}
</style>
