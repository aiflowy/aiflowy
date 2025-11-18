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
  positionName: '',
  positionCode: '',
  sortNo: '',
  status: '',
  remark: '',
});
const btnLoading = ref(false);
const rules = ref({
  deptId: [{ required: true, message: '请输入部门ID', trigger: 'blur' }],
  positionName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
  ],
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
          isAdd.value ? 'api/v1/sysPosition/save' : 'api/v1/sysPosition/update',
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
      <ElFormItem prop="positionName" label="岗位名称">
        <ElInput v-model.trim="entity.positionName" />
      </ElFormItem>
      <ElFormItem prop="positionCode" label="岗位编码">
        <ElInput v-model.trim="entity.positionCode" />
      </ElFormItem>
      <ElFormItem prop="sortNo" label="排序">
        <ElInput v-model.trim="entity.sortNo" />
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
