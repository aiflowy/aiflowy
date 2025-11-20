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
  password: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  accountType: [
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
  entity.value.avatars = [
    'https://static.agentscenter.cn/public//1/2025/11/20/8e11e271-d863-47ad-8817-0dc3f157becc/0ec0626f1c2b425888f928efc0997eb8.png',
    'https://static.agentscenter.cn/public//1/2025/11/20/07d63759-01d8-4b03-8b15-c2d03cd9e0ab/6C29D7C8F836BEFABCEA3FA87F351BA9.jpg',
    'https://static.agentscenter.cn/public//1/2025/11/20/60194bc4-097a-49b3-bef7-b5a071cdb4e1/99bfe6c53d4855e0 (1).jpg',
  ];
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
      <ElFormItem prop="deptId" :label="$t('sysAccount.deptId')">
        <ElInput v-model.trim="entity.deptId" />
      </ElFormItem>
      <ElFormItem prop="loginName" :label="$t('sysAccount.loginName')">
        <ElInput v-model.trim="entity.loginName" />
      </ElFormItem>
      <ElFormItem prop="password" :label="$t('sysAccount.password')">
        <ElInput v-model.trim="entity.password" />
      </ElFormItem>
      <ElFormItem prop="accountType" :label="$t('sysAccount.accountType')">
        <ElInput v-model.trim="entity.accountType" />
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
      <ElFormItem prop="avatar" :label="$t('sysAccount.avatar')">
        <Cropper v-model="entity.avatar" crop />
      </ElFormItem>
      <ElFormItem prop="dataScope" :label="$t('sysAccount.dataScope')">
        <ElInput v-model.trim="entity.dataScope" />
      </ElFormItem>
      <ElFormItem prop="deptIdList" :label="$t('sysAccount.deptIdList')">
        <ElInput v-model.trim="entity.deptIdList" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('sysAccount.status')">
        <ElInput v-model.trim="entity.status" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysAccount.remark')">
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
