<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

const modelType = ref('');
const vectorDimension = ref('');
const formDataRef = ref();
const dialogVisible = ref(false);
const formData = reactive({
  vectorEmbedModelId: '',
  collectionName: '',
  vectorDimension: 1024,
  vectorDatabase: {},
});
defineExpose({
  openDialog(row: any) {
    formDataRef.value?.resetFields();
    formData.vectorDatabase = row;
    dialogVisible.value = true;
  },
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
    .post('/api/v1/vectorDatabase/verifyVectorConfig', {
      vectorEmbedModelId: formData.vectorEmbedModelId,
      collectionName: formData.collectionName,
      dimensions: formData.vectorDimension,
      vectorDatabase: formData.vectorDatabase,
    })
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
const embeddingLlmList = ref<any>([]);
const getEmbeddingLlmListData = async () => {
  try {
    const url = `/api/v1/model/list?modelType=embeddingModel`;
    const res = await api.get(url, {});
    if (res.errorCode === 0) {
      embeddingLlmList.value = res.data;
    }
  } catch (error) {
    ElMessage.error($t('message.apiError'));
    console.error('获取嵌入模型列表失败：', error);
  }
};
onMounted(async () => {
  await getEmbeddingLlmListData();
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="$t('vectorDatabase.chooseVectorModel')"
    :close-on-click-modal="false"
    align-center
    width="482"
  >
    <ElForm
      ref="formDataRef"
      :model="formData"
      status-icon
      :rules="rules"
      label-width="120px"
    >
      <ElFormItem
        prop="vectorEmbedModelId"
        :label="$t('vectorDatabase.embeddingModel')"
      >
        <ElSelect
          v-model="formData.vectorEmbedModelId"
          :placeholder="$t('documentCollection.placeholder.embedLlm')"
        >
          <ElOption
            v-for="item in embeddingLlmList"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="$t('vectorDatabase.collectionName')">
        <ElInput v-model="formData.collectionName" />
      </ElFormItem>
      <ElFormItem :label="$t('documentCollection.dimensionOfVectorModel')">
        <ElInput v-model="formData.vectorDimension" type="number" />
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
