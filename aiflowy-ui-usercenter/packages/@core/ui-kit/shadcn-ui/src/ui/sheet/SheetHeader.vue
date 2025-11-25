<script setup lang="ts">
import { cn } from '@aiflowy-core/shared/utils';

const props = defineProps<{ class?: any }>();
</script>

<template>
  <div :class="cn('flex flex-col text-center sm:text-left', props.class)">
    <slot></slot>
  </div>
</template>
