<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { computed, ref } from 'vue';

import { Position } from '@element-plus/icons-vue';
import {
  ElAlert,
  ElButton,
  ElCheckboxGroup,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElRadioGroup,
  ElSelect,
} from 'element-plus';

import { sse } from '#/api/request';
import { $t } from '#/locales';

export type WorkflowFormProps = {
  onExecuting?: (values: any) => void;
  onSubmit?: (values: any) => void;
  workflowId: any;
  workflowParams: any;
};
const props = withDefaults(defineProps<WorkflowFormProps>(), {
  onExecuting: () => {
    console.warn('no execute method');
  },
  onSubmit: () => {
    console.warn('no submit method');
  },
});
const runForm = ref<FormInstance>();
const runParams = ref<any>({});
const submitLoading = ref(false);
const parameters = computed(() => {
  return props.workflowParams.parameters;
});
const { postSse } = sse();
function getContentType(item: any) {
  return item.contentType || 'text';
}
function isResource(contentType: any) {
  return ['audio', 'file', 'image', 'video'].includes(contentType);
}
function getCheckboxOptions(item: any) {
  if (item.enums) {
    return (
      item.enums?.map((option: any) => ({
        label: option,
        value: option,
      })) || []
    );
  }
  return [];
}
function submit() {
  runForm.value?.validate((valid) => {
    if (valid) {
      const data = {
        id: props.workflowId,
        variables: {
          ...runParams.value,
        },
      };
      props.onSubmit?.(runParams.value);
      submitLoading.value = true;
      postSse('/api/v1/aiWorkflow/tryRunningStream', data, {
        onMessage: (message) => {
          if (message.data) {
            const msg = JSON.parse(message.data).content;
            if (msg.status === 'execOnce') {
              ElMessage.warning('流程已执行完毕，请重新发起。');
            } else {
              props.onExecuting?.(message);
            }
          }
        },
        onFinished: () => {
          submitLoading.value = false;
        },
      });
    }
  });
}
</script>

<template>
  <ElForm label-position="top" ref="runForm" :model="runParams">
    <ElFormItem
      v-for="(item, idx) in parameters"
      :prop="item.name"
      :key="idx"
      :label="item.formLabel || item.name"
      :rules="
        item.required
          ? [{ required: true, message: $t('message.required') }]
          : []
      "
    >
      <template v-if="getContentType(item) === 'text'">
        <ElInput
          v-if="item.formType === 'input' || !item.formType"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
        />
        <ElSelect
          v-if="item.formType === 'select'"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
          :options="getCheckboxOptions(item)"
          clearable
        />
        <ElInput
          v-if="item.formType === 'textarea'"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
          :rows="3"
          type="textarea"
        />
        <ElRadioGroup
          v-if="item.formType === 'radio'"
          v-model="runParams[item.name]"
          :options="getCheckboxOptions(item)"
        />
        <ElCheckboxGroup
          v-if="item.formType === 'checkbox'"
          v-model="runParams[item.name]"
          :options="getCheckboxOptions(item)"
        />
      </template>
      <template v-if="getContentType(item) === 'other'">
        <ElInput
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
        />
      </template>
      <template v-if="isResource(getContentType(item))">
        选择资源组件
      </template>
      <ElAlert v-if="item.formDescription" type="info" style="margin-top: 5px">
        {{ item.formDescription }}
      </ElAlert>
    </ElFormItem>
    <ElFormItem>
      <ElButton
        type="primary"
        @click="submit"
        :loading="submitLoading"
        :icon="Position"
      >
        {{ $t('button.run') }}
      </ElButton>
    </ElFormItem>
  </ElForm>
</template>

<style scoped></style>
