<script setup lang="ts">
import { ref, watch } from 'vue';

import { preferences } from '@aiflowy/preferences';

import { ElEmpty } from 'element-plus';
import { JsonViewer } from 'vue3-json-viewer';

import 'vue3-json-viewer/dist/vue3-json-viewer.css';

defineProps({
  value: {
    required: true,
  },
});

const themeMode = ref(preferences.theme.mode);
watch(
  () => preferences.theme.mode,
  (newVal) => {
    themeMode.value = newVal;
  },
);
</script>

<template>
  <div class="res-container">
    <JsonViewer v-if="value" :value="value" copyable :theme="themeMode" />
    <ElEmpty
      :image="`/empty${preferences.theme.mode === 'dark' ? '-dark' : ''}.png`"
      v-else
    />
  </div>
</template>

<style scoped>
.res-container {
  border: 1px solid var(--el-border-color);
  border-radius: var(--el-border-radius-base);
  padding: 10px;
}
</style>
