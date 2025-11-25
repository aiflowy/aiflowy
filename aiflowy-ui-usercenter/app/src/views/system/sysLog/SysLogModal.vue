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
  accountId: '',
  actionName: '',
  actionType: '',
  actionClass: '',
  actionMethod: '',
  actionUrl: '',
  actionIp: '',
  actionParams: '',
  actionBody: '',
  status: '',
});
const btnLoading = ref(false);
const rules = ref({});
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
          isAdd.value ? 'api/v1/sysLog/save' : 'api/v1/sysLog/update',
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
      <ElFormItem prop="accountId" :label="$t('sysLog.accountId')">
        <ElInput v-model.trim="entity.accountId" />
      </ElFormItem>
      <ElFormItem prop="actionName" :label="$t('sysLog.actionName')">
        <ElInput v-model.trim="entity.actionName" />
      </ElFormItem>
      <ElFormItem prop="actionType" :label="$t('sysLog.actionType')">
        <ElInput v-model.trim="entity.actionType" />
      </ElFormItem>
      <ElFormItem prop="actionClass" :label="$t('sysLog.actionClass')">
        <ElInput v-model.trim="entity.actionClass" />
      </ElFormItem>
      <ElFormItem prop="actionMethod" :label="$t('sysLog.actionMethod')">
        <ElInput v-model.trim="entity.actionMethod" />
      </ElFormItem>
      <ElFormItem prop="actionUrl" :label="$t('sysLog.actionUrl')">
        <ElInput v-model.trim="entity.actionUrl" />
      </ElFormItem>
      <ElFormItem prop="actionIp" :label="$t('sysLog.actionIp')">
        <ElInput v-model.trim="entity.actionIp" />
      </ElFormItem>
      <ElFormItem prop="actionParams" :label="$t('sysLog.actionParams')">
        <ElInput v-model.trim="entity.actionParams" />
      </ElFormItem>
      <ElFormItem prop="actionBody" :label="$t('sysLog.actionBody')">
        <ElInput v-model.trim="entity.actionBody" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('sysLog.status')">
        <ElInput v-model.trim="entity.status" />
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
