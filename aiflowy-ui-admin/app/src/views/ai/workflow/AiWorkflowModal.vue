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
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import Cropper from '#/components/upload/Cropper.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
// vue
onMounted(() => {});
defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
// variables
const dialogVisible = ref(false);
const isAdd = ref(true);
const entity = ref<any>({
  alias: '',
  deptId: '',
  title: '',
  description: '',
  icon: '',
  content: '',
  englishName: '',
});
const btnLoading = ref(false);
const rules = ref({
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
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
          isAdd.value ? 'api/v1/aiWorkflow/save' : 'api/v1/aiWorkflow/update',
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
  >
    <ElForm
      label-width="120px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="icon" :label="$t('aiWorkflow.icon')">
        <Cropper v-model="entity.icon" crop />
      </ElFormItem>
      <ElFormItem prop="title" :label="$t('aiWorkflow.title')">
        <ElInput v-model.trim="entity.title" />
      </ElFormItem>
      <ElFormItem prop="alias" :label="$t('aiWorkflow.alias')">
        <ElInput v-model.trim="entity.alias" />
      </ElFormItem>
      <ElFormItem prop="englishName" :label="$t('aiWorkflow.englishName')">
        <ElInput v-model.trim="entity.englishName" />
      </ElFormItem>
      <ElFormItem prop="description" :label="$t('aiWorkflow.description')">
        <ElInput v-model.trim="entity.description" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('aiWorkflow.status')">
        <DictSelect v-model="entity.status" dict-code="dataStatus" />
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
