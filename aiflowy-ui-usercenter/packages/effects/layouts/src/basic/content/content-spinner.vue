<script lang="ts" setup>
import { AIFlowySpinner } from '@aiflowy-core/shadcn-ui';

import { useContentSpinner } from './use-content-spinner';

defineOptions({ name: 'LayoutContentSpinner' });

const { spinning } = useContentSpinner();
</script>
<template>
  <AIFlowySpinner :spinning="spinning" />
</template>
