<script setup lang="ts">
import type { SupportedLanguagesType } from '@aiflowy/locales';

import { SUPPORT_LANGUAGES } from '@aiflowy/constants';
import { Languages } from '@aiflowy/icons';
import { loadLocaleMessages } from '@aiflowy/locales';
import { preferences, updatePreferences } from '@aiflowy/preferences';

import { AIFlowyDropdownRadioMenu, AIFlowyIconButton } from '@aiflowy-core/shadcn-ui';

defineOptions({
  name: 'LanguageToggle',
});

async function handleUpdate(value: string | undefined) {
  if (!value) return;
  const locale = value as SupportedLanguagesType;
  updatePreferences({
    app: {
      locale,
    },
  });
  await loadLocaleMessages(locale);
}
</script>

<template>
  <div>
    <AIFlowyDropdownRadioMenu
      :menus="SUPPORT_LANGUAGES"
      :model-value="preferences.app.locale"
      @update:model-value="handleUpdate"
    >
      <AIFlowyIconButton class="hover:animate-[shrink_0.3s_ease-in-out]">
        <Languages class="text-foreground size-4" />
      </AIFlowyIconButton>
    </AIFlowyDropdownRadioMenu>
  </div>
</template>
