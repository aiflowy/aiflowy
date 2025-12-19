<script setup lang="ts">
import { reactive, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

const options = ref<any[]>([]);
const getLlmList = (providerId: string) => {
  api.get(`/api/v1/aiLlm/list?providerId=${providerId}`, {}).then((res) => {
    if (res.errorCode === 0) {
      options.value = res.data;
    }
  });
};
const formDataRef = ref();
const dialogVisible = ref(false);
defineExpose({
  openDialog(providerId: string) {
    formDataRef.value?.resetFields();
    getLlmList(providerId);
    dialogVisible.value = true;
  },
});
const formData = reactive({
  llmId: '',
});

const rules = {
  llmId: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'change',
    },
  ],
};

const save = async () => {
  btnLoading.value = true;
  await formDataRef.value.validate();
  api
    .get(`/api/v1/aiLlm/verifyLlmConfig?id=${formData.llmId}`, {})
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('llm.testSuccess'));
      }
      btnLoading.value = false;
    });
};
const btnLoading = ref(false);
const closeDialog = () => {
  dialogVisible.value = false;
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="$t('llm.verifyLlmTitle')"
    :close-on-click-modal="false"
    align-center
    width="482"
  >
    <ElForm ref="formDataRef" :model="formData" status-icon :rules="rules">
      <ElFormItem prop="llmId">
        <ElSelect v-model="formData.llmId">
          <ElOption
            v-for="item in options"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.confirm') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.headers-container-reduce {
  align-items: center;
}
.addHeadersBtn {
  width: 100%;
  border-style: dashed;
  border-color: var(--el-color-primary);
  border-radius: 8px;
  margin-top: 8px;
}
.head-con-content {
  margin-bottom: 8px;
  align-items: center;
}
</style>
