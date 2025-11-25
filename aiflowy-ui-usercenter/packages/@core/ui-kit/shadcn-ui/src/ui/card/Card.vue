<script setup lang="ts">
import { cn } from '@aiflowy-core/shared/utils';

const props = defineProps<{
  class?: any;
}>();
</script>

<template>
  <div
    :class="
      cn(
        'bg-card text-card-foreground border-border rounded-xl border',
        props.class,
      )
    "
  >
    <slot></slot>
  </div>
</template>
