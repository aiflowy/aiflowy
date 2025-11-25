<script lang="ts" setup>
import type { DropdownMenuProps } from '@aiflowy-core/shadcn-ui';

import { ChevronDown } from '@aiflowy-core/icons';
import { AIFlowyDropdownMenu } from '@aiflowy-core/shadcn-ui';

defineProps<DropdownMenuProps>();
</script>

<template>
  <AIFlowyDropdownMenu :menus="menus" :modal="false">
    <div
      class="flex-center hover:bg-muted hover:text-foreground text-muted-foreground border-border h-full cursor-pointer border-l px-2 text-lg font-semibold"
    >
      <ChevronDown class="size-4" />
    </div>
  </AIFlowyDropdownMenu>
</template>
