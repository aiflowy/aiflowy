<script lang="ts" setup>
import type { Preferences } from '@aiflowy/preferences';

import { onMounted } from 'vue';

import { useElementPlusDesignTokens } from '@aiflowy/hooks';
import { preferences, updatePreferences } from '@aiflowy/preferences';

import { ElConfigProvider } from 'element-plus';
import { assign, tryit } from 'radash';

import { elementLocale } from '#/locales';

import { api } from './api/request';

defineOptions({ name: 'App' });

useElementPlusDesignTokens();

onMounted(async () => {
  const [, res] = await tryit(api.get)('/api/v1/public/getUiConfig');
  if (res && res.errorCode === 0) {
    updatePreferences(assign<Preferences>(res.data, preferences));
  }
});
</script>

<template>
  <ElConfigProvider :locale="elementLocale">
    <RouterView />
  </ElConfigProvider>
</template>
