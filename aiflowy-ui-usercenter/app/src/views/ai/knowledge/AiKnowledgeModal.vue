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

onMounted(() => {
  api.get('/api/v1/aiLlm/list?supportEmbed=true').then((res) => {
    embeddingLlmList.value = res.data;
  });
  api.get('/api/v1/aiLlm/list?supportRerankerLlmList=true').then((res) => {
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
  vectorEmbedLlmId: '',
  options: '',
  rerankLlmId: '',
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
  vectorEmbedLlmId: [
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
          isAdd.value ? 'api/v1/aiKnowledge/save' : 'api/v1/aiKnowledge/update',
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
        :label="$t('aiKnowledge.icon')"
        style="display: flex; align-items: center"
      >
        <UploadAvatar v-model="entity.icon" />
      </ElFormItem>
      <ElFormItem prop="title" :label="$t('aiKnowledge.title')">
        <ElInput
          v-model.trim="entity.title"
          :placeholder="$t('aiKnowledge.placeholder.title')"
        />
      </ElFormItem>
      <ElFormItem prop="alias" :label="$t('aiKnowledge.alias')">
        <ElInput v-model.trim="entity.alias" />
      </ElFormItem>
      <ElFormItem prop="englishName" :label="$t('aiKnowledge.englishName')">
        <ElInput v-model.trim="entity.englishName" />
      </ElFormItem>
      <ElFormItem prop="description" :label="$t('aiKnowledge.description')">
        <ElInput
          v-model.trim="entity.description"
          :rows="4"
          type="textarea"
          :placeholder="$t('aiKnowledge.placeholder.description')"
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreEnable"
        :label="$t('aiKnowledge.vectorStoreEnable')"
      >
        <ElSwitch v-model="entity.vectorStoreEnable" />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreType"
        :label="$t('aiKnowledge.vectorStoreType')"
      >
        <ElSelect
          v-model="entity.vectorStoreType"
          :placeholder="$t('aiKnowledge.placeholder.vectorStoreType')"
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
        :label="$t('aiKnowledge.vectorStoreCollection')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreCollection"
          :placeholder="$t('aiKnowledge.placeholder.vectorStoreCollection')"
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreConfig"
        :label="$t('aiKnowledge.vectorStoreConfig')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreConfig"
          :rows="4"
          type="textarea"
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorEmbedLlmId"
        :label="$t('aiKnowledge.vectorEmbedLlmId')"
      >
        <ElSelect
          v-model="entity.vectorEmbedLlmId"
          :placeholder="$t('aiKnowledge.placeholder.embedLlm')"
        >
          <ElOption
            v-for="item in embeddingLlmList"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem prop="rerankLlmId" :label="$t('aiKnowledge.rerankLlmId')">
        <ElSelect
          v-model="entity.rerankLlmId"
          :placeholder="$t('aiKnowledge.placeholder.rerankLlm')"
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
        :label="$t('aiKnowledge.searchEngineEnable')"
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
