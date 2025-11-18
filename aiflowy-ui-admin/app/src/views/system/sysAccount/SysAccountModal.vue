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
  deptId: [{ required: true, message: '请输入部门ID', trigger: 'blur' }],
  loginName: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  accountType: [{ required: true, message: '请输入账户类型', trigger: 'blur' }],
  status: [{ required: true, message: '请输入数据状态', trigger: 'blur' }],
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
      <ElFormItem prop="deptId" label="部门ID">
        <ElInput v-model.trim="entity.deptId" />
      </ElFormItem>
      <ElFormItem prop="loginName" label="登录账号">
        <ElInput v-model.trim="entity.loginName" />
      </ElFormItem>
      <ElFormItem prop="password" label="密码">
        <ElInput v-model.trim="entity.password" />
      </ElFormItem>
      <ElFormItem prop="accountType" label="账户类型">
        <ElInput v-model.trim="entity.accountType" />
      </ElFormItem>
      <ElFormItem prop="nickname" label="昵称">
        <ElInput v-model.trim="entity.nickname" />
      </ElFormItem>
      <ElFormItem prop="mobile" label="手机电话">
        <ElInput v-model.trim="entity.mobile" />
      </ElFormItem>
      <ElFormItem prop="email" label="邮件">
        <ElInput v-model.trim="entity.email" />
      </ElFormItem>
      <ElFormItem prop="avatar" label="账户头像">
        <ElInput v-model.trim="entity.avatar" />
      </ElFormItem>
      <ElFormItem prop="dataScope" label="数据权限类型">
        <ElInput v-model.trim="entity.dataScope" />
      </ElFormItem>
      <ElFormItem prop="deptIdList" label="自定义部门权限">
        <ElInput v-model.trim="entity.deptIdList" />
      </ElFormItem>
      <ElFormItem prop="status" label="数据状态">
        <ElInput v-model.trim="entity.status" />
      </ElFormItem>
      <ElFormItem prop="remark" label="备注">
        <ElInput v-model.trim="entity.remark" />
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
