<script setup lang="ts">
import { ElAvatar } from 'element-plus';

import defaultBotAvatar from '#/assets/ai/bot/defaultBotAvatar.png';

const props = defineProps<{
  size?: number;
  src?: string;
}>();
</script>

<template>
  <ElAvatar :src="props.src ?? defaultBotAvatar" :size="props.size ?? 36" />
</template>
