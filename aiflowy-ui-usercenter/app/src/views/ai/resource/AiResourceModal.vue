<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { getResourceType } from '@aiflowy/utils';

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
import Upload from '#/components/upload/Upload.vue';
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
  deptId: '',
  resourceType: '',
  resourceName: '',
  suffix: '',
  resourceUrl: '',
  origin: '',
  status: '',
  options: '',
  fileSize: '',
});
const btnLoading = ref(false);
const rules = ref({
  deptId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  resourceType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  resourceName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  suffix: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  resourceUrl: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  origin: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  status: [
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
          isAdd.value ? 'api/v1/aiResource/save' : 'api/v1/aiResource/update',
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
function beforeUpload(f: any) {
  const fName = f?.name?.split('.')[0];
  const fExt = f?.name?.split('.')[1];
  entity.value.resourceType = getResourceType(fExt);
  entity.value.resourceName = fName;
  entity.value.suffix = fExt;
  entity.value.fileSize = f.size;
  entity.value.origin = 0;
}
function uploadSuccess(res: any) {
  entity.value.resourceUrl = res;
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
      <ElFormItem prop="resourceUrl" :label="$t('aiResource.resourceUrl')">
        <Upload @before-upload="beforeUpload" @success="uploadSuccess" />
      </ElFormItem>
      <ElFormItem prop="origin" :label="$t('aiResource.origin')">
        <DictSelect v-model="entity.origin" dict-code="resourceOriginType" />
      </ElFormItem>
      <ElFormItem prop="resourceType" :label="$t('aiResource.resourceType')">
        <DictSelect v-model="entity.resourceType" dict-code="resourceType" />
      </ElFormItem>
      <ElFormItem prop="resourceName" :label="$t('aiResource.resourceName')">
        <ElInput v-model.trim="entity.resourceName" />
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
