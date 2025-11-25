<script lang="ts" setup>
import { Settings } from '@aiflowy/icons';

import { AIFlowyIconButton } from '@aiflowy-core/shadcn-ui';

import Preferences from './preferences.vue';

const emit = defineEmits<{ clearPreferencesAndLogout: [] }>();

function clearPreferencesAndLogout() {
  emit('clearPreferencesAndLogout');
}
</script>
<template>
  <Preferences @clear-preferences-and-logout="clearPreferencesAndLogout">
    <AIFlowyIconButton class="hover:animate-[shrink_0.3s_ease-in-out]">
      <Settings class="text-foreground size-4" />
    </AIFlowyIconButton>
  </Preferences>
</template>
