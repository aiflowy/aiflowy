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
  deptId: '',
  loginName: '',
  password: '',
  accountType: '',
  nickname: '',
  mobile: '',
  email: '',
  avatar: '',
  dataScope: '',
  deptIdList: '',
  status: '',
  remark: '',
});
const btnLoading = ref(false);
const rules = ref({
  deptId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  loginName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  nickname: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  password: [
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
          isAdd.value ? 'api/v1/sysAccount/save' : 'api/v1/sysAccount/update',
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
      <ElFormItem prop="avatar" :label="$t('sysAccount.avatar')">
        <Cropper v-model="entity.avatar" crop />
      </ElFormItem>
      <ElFormItem prop="deptId" :label="$t('sysAccount.deptId')">
        <DictSelect v-model="entity.deptId" dict-code="sysDept" />
      </ElFormItem>
      <ElFormItem prop="loginName" :label="$t('sysAccount.loginName')">
        <ElInput v-model.trim="entity.loginName" />
      </ElFormItem>
      <ElFormItem
        v-if="isAdd"
        prop="password"
        :label="$t('sysAccount.password')"
      >
        <ElInput v-model.trim="entity.password" />
      </ElFormItem>
      <ElFormItem prop="nickname" :label="$t('sysAccount.nickname')">
        <ElInput v-model.trim="entity.nickname" />
      </ElFormItem>
      <ElFormItem prop="mobile" :label="$t('sysAccount.mobile')">
        <ElInput v-model.trim="entity.mobile" />
      </ElFormItem>
      <ElFormItem prop="email" :label="$t('sysAccount.email')">
        <ElInput v-model.trim="entity.email" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('sysAccount.status')">
        <DictSelect v-model="entity.status" dict-code="dataStatus" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysAccount.remark')">
        <ElInput v-model.trim="entity.remark" />
      </ElFormItem>
      <ElFormItem prop="roleIds" :label="$t('sysAccount.roleIds')">
        <DictSelect multiple v-model="entity.roleIds" dict-code="sysRole" />
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
