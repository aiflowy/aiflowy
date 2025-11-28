<script setup lang="ts">
import { ref, watch } from 'vue';

import { preferences } from '@aiflowy/preferences';

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
  <JsonViewer :value="value || ''" copyable :theme="themeMode" />
</template>

<style scoped></style>
