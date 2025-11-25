<script setup lang="ts">
import type { AuthPageLayoutType } from '@aiflowy/types';

import type { AIFlowyDropdownMenuItem } from '@aiflowy-core/shadcn-ui';

import { computed } from 'vue';

import { InspectionPanel, PanelLeft, PanelRight } from '@aiflowy/icons';
import { $t } from '@aiflowy/locales';
import {
  preferences,
  updatePreferences,
  usePreferences,
} from '@aiflowy/preferences';

import { AIFlowyDropdownRadioMenu, AIFlowyIconButton } from '@aiflowy-core/shadcn-ui';

defineOptions({
  name: 'AuthenticationLayoutToggle',
});

const menus = computed((): AIFlowyDropdownMenuItem[] => [
  {
    icon: PanelLeft,
    label: $t('authentication.layout.alignLeft'),
    value: 'panel-left',
  },
  {
    icon: InspectionPanel,
    label: $t('authentication.layout.center'),
    value: 'panel-center',
  },
  {
    icon: PanelRight,
    label: $t('authentication.layout.alignRight'),
    value: 'panel-right',
  },
]);

const { authPanelCenter, authPanelLeft, authPanelRight } = usePreferences();

function handleUpdate(value: string | undefined) {
  if (!value) return;
  updatePreferences({
    app: {
      authPageLayout: value as AuthPageLayoutType,
    },
  });
}
</script>

<template>
  <AIFlowyDropdownRadioMenu
    :menus="menus"
    :model-value="preferences.app.authPageLayout"
    @update:model-value="handleUpdate"
  >
    <AIFlowyIconButton>
      <PanelRight v-if="authPanelRight" class="size-4" />
      <PanelLeft v-if="authPanelLeft" class="size-4" />
      <InspectionPanel v-if="authPanelCenter" class="size-4" />
    </AIFlowyIconButton>
  </AIFlowyDropdownRadioMenu>
</template>
