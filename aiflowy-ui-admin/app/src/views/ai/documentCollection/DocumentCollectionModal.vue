<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch,
} from 'element-plus';

import { api } from '#/api/request';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
const embeddingLlmList = ref<any>([]);
const rerankerLlmList = ref<any>([]);
const getEmbeddingLlmListData = async () => {
  const url = `/api/v1/model/list?modelType=embeddingModel`;
  api.get(url, {}).then((res) => {
    if (res.errorCode === 0) {
      embeddingLlmList.value = res.data;
    }
  });
};
onMounted(() => {
  getEmbeddingLlmListData();
  api.get('/api/v1/model/list?modelType=embeddingModel').then((res) => {
    rerankerLlmList.value = res.data;
  });
});
defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
// variables
const dialogVisible = ref(false);
const isAdd = ref(true);
const vecotrDatabaseList = ref<any>([
  { value: 'milvus', label: 'Milvus' },
  { value: 'redis', label: 'Redis' },
  { value: 'opensearch', label: 'OpenSearch' },
  { value: 'elasticsearch', label: 'ElasticSearch' },
  { value: 'aliyun', label: '阿里云' },
  { value: 'qcloud', label: '腾讯云' },
]);
const entity = ref<any>({
  alias: '',
  deptId: '',
  icon: '',
  title: '',
  description: '',
  slug: '',
  vectorStoreEnable: '',
  vectorStoreType: '',
  vectorStoreCollection: '',
  vectorStoreConfig: '',
  vectorEmbedModelId: '',
  options: '',
  rerankModelId: '',
  searchEngineEnable: '',
  englishName: '',
});
const btnLoading = ref(false);
const rules = ref({
  deptId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  englishName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  description: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  vectorStoreType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorStoreCollection: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorStoreConfig: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorEmbedModelId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
});
// functions
function openDialog(row: any) {
  if (row.id) {
    isAdd.value = false;
  }
  entity.value = row;
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      api
        .post(
          isAdd.value
            ? 'api/v1/documentCollection/save'
            : 'api/v1/documentCollection/update',
          entity.value,
        )
        .then((res) => {
          btnLoading.value = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            emit('reload');
            closeDialog();
          }
        })
        .catch(() => {
          btnLoading.value = false;
        });
    }
  });
}
function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = {};
  dialogVisible.value = false;
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
  >
    <ElForm
      label-width="150px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem
        prop="icon"
        :label="$t('documentCollection.icon')"
        style="display: flex; align-items: center"
      >
        <UploadAvatar v-model="entity.icon" />
      </ElFormItem>
      <ElFormItem prop="title" :label="$t('documentCollection.title')">
        <ElInput
          v-model.trim="entity.title"
          :placeholder="$t('documentCollection.placeholder.title')"
        />
      </ElFormItem>
      <ElFormItem prop="alias" :label="$t('documentCollection.alias')">
        <ElInput v-model.trim="entity.alias" />
      </ElFormItem>
      <ElFormItem
        prop="englishName"
        :label="$t('documentCollection.englishName')"
      >
        <ElInput v-model.trim="entity.englishName" />
      </ElFormItem>
      <ElFormItem
        prop="description"
        :label="$t('documentCollection.description')"
      >
        <ElInput
          v-model.trim="entity.description"
          :rows="4"
          type="textarea"
          :placeholder="$t('documentCollection.placeholder.description')"
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreEnable"
        :label="$t('documentCollection.vectorStoreEnable')"
      >
        <ElSwitch v-model="entity.vectorStoreEnable" />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreType"
        :label="$t('documentCollection.vectorStoreType')"
      >
        <ElSelect
          v-model="entity.vectorStoreType"
          :placeholder="$t('documentCollection.placeholder.vectorStoreType')"
        >
          <ElOption
            v-for="item in vecotrDatabaseList"
            :key="item.value"
            :label="item.label"
            :value="item.value || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreCollection"
        :label="$t('documentCollection.vectorStoreCollection')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreCollection"
          :placeholder="
            $t('documentCollection.placeholder.vectorStoreCollection')
          "
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreConfig"
        :label="$t('documentCollection.vectorStoreConfig')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreConfig"
          :rows="4"
          type="textarea"
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorEmbedModelId"
        :label="$t('documentCollection.vectorEmbedLlmId')"
      >
        <ElSelect
          v-model="entity.vectorEmbedModelId"
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
      <ElFormItem
        prop="rerankLlmId"
        :label="$t('documentCollection.rerankLlmId')"
      >
        <ElSelect
          v-model="entity.rerankLlmId"
          :placeholder="$t('documentCollection.placeholder.rerankLlm')"
        >
          <ElOption
            v-for="item in rerankerLlmList"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="searchEngineEnable"
        :label="$t('documentCollection.searchEngineEnable')"
      >
        <ElSwitch v-model="entity.searchEngineEnable" />
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
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped></style>
