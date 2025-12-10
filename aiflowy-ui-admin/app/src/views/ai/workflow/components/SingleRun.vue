<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import { Position } from '@element-plus/icons-vue';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage } from 'element-plus';

import { api } from '#/api/request';
import ShowJson from '#/components/json/ShowJson.vue';
import { $t } from '#/locales';

interface Props {
  workflowId: any;
  node: any;
}

const props = defineProps<Props>();

const singleRunForm = ref<FormInstance>();
const runParams = ref<any>({});
const submitLoading = ref(false);
const result = ref<any>('');
function submit() {
  singleRunForm.value?.validate((valid) => {
    if (valid) {
      const params = {
        workflowId: props.workflowId,
        nodeId: props.node.id,
        variables: runParams.value,
      };
      submitLoading.value = true;
      api.post('/api/v1/aiWorkflow/singleRun', params).then((res) => {
        submitLoading.value = false;
        result.value = res.data;
        if (res.errorCode === 0) {
          ElMessage.success(res.message);
        } else {
          ElMessage.error(res.message);
        }
      });
    }
  });
}
</script>

<template>
  <div>
    <ElForm label-position="top" ref="singleRunForm" :model="runParams">
      <ElFormItem
        v-for="(item, idx) in node?.data.parameters"
        :prop="item.name"
        :key="idx"
        :label="item.description || item.name"
        :rules="[{ required: true, message: $t('message.required') }]"
      >
        <ElInput
          v-if="item.formType === 'input' || !item.formType"
          v-model="runParams[item.name]"
          :placeholder="item.formPlaceholder"
        />
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
    <div class="mb-2.5 mt-2.5 font-semibold">
      {{ $t('aiWorkflow.result') }}：
    </div>
    <ShowJson :value="result" />
  </div>
</template>

<style scoped></style>
