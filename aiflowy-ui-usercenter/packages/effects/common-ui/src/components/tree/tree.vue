<script setup lang="ts">
import type { TreeProps } from '@aiflowy-core/shadcn-ui';

import { Inbox } from '@aiflowy/icons';
import { $t } from '@aiflowy/locales';

import { treePropsDefaults, AIFlowyTree } from '@aiflowy-core/shadcn-ui';

const props = withDefaults(defineProps<TreeProps>(), treePropsDefaults());
</script>

<template>
  <AIFlowyTree v-if="props.treeData?.length > 0" v-bind="props">
    <template v-for="(_, key) in $slots" :key="key" #[key]="slotProps">
      <slot :name="key" v-bind="slotProps"> </slot>
    </template>
  </AIFlowyTree>
  <div
    v-else
    class="flex-col-center text-muted-foreground cursor-pointer rounded-lg border p-10 text-sm font-medium"
  >
    <Inbox class="size-10" />
    <div class="mt-1">{{ $t('common.noData') }}</div>
  </div>
</template>
