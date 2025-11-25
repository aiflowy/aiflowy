<script setup lang="ts">
import type { Recordable } from '@aiflowy/types';

import type { AIFlowyFormSchema } from '@aiflowy-core/form-ui';

import { computed, reactive } from 'vue';

import { useAIFlowyForm } from '@aiflowy-core/form-ui';
import { AIFlowyButton } from '@aiflowy-core/shadcn-ui';

interface Props {
  formSchema?: AIFlowyFormSchema[];
}

const props = withDefaults(defineProps<Props>(), {
  formSchema: () => [],
});

const emit = defineEmits<{
  submit: [Recordable<any>];
}>();

const [Form, formApi] = useAIFlowyForm(
  reactive({
    commonConfig: {
      // 所有表单项
      componentProps: {
        class: 'w-full',
      },
    },
    layout: 'horizontal',
    schema: computed(() => props.formSchema),
    showDefaultActions: false,
  }),
);

async function handleSubmit() {
  const { valid } = await formApi.validate();
  const values = await formApi.getValues();
  if (valid) {
    emit('submit', values);
  }
}

defineExpose({
  getFormApi: () => formApi,
});
</script>
<template>
  <div @keydown.enter.prevent="handleSubmit">
    <Form />
    <AIFlowyButton type="submit" class="mt-4" @click="handleSubmit">
      更新基本信息
    </AIFlowyButton>
  </div>
</template>
