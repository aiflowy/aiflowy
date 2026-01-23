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
  api.get(`/api/v1/model/list?providerId=${providerId}`, {}).then((res) => {
    if (res.errorCode === 0) {
      options.value = res.data;
    }
  });
};
const modelType = ref('');
const vectorDimension = ref('');
const formDataRef = ref();
const dialogVisible = ref(false);
defineExpose({
  openDialog(providerId: string) {
    formDataRef.value?.resetFields();
    modelType.value = '';
    vectorDimension.value = '';
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
    .get(`/api/v1/model/verifyLlmConfig?id=${formData.llmId}`, {})
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('llm.testSuccess'));
        if (modelType.value === 'embeddingModel' && res?.data?.dimension) {
          vectorDimension.value = res?.data?.dimension;
        }
      }
      btnLoading.value = false;
    });
};
const btnLoading = ref(false);
const closeDialog = () => {
  dialogVisible.value = false;
};
const getModelInfo = (id: string) => {
  options.value.forEach((item: any) => {
    if (item.id === id) {
      modelType.value = item.modelType;
    }
  });
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
      <ElFormItem prop="llmId" :label="$t('llm.modelToBeTested')">
        <ElSelect v-model="formData.llmId" @change="getModelInfo">
          <ElOption
            v-for="item in options"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        v-if="modelType === 'embeddingModel' && vectorDimension"
        :label="$t('documentCollection.dimensionOfVectorModel')"
        label-width="100px"
      >
        {{ vectorDimension }}
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
