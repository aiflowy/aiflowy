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
  parentId: '',
  ancestors: '',
  deptName: '',
  deptCode: '',
  sortNo: '',
  status: '',
  remark: '',
});
const btnLoading = ref(false);
const rules = ref({
  parentId: [{ required: true, message: '请输入父级ID', trigger: 'blur' }],
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
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
          isAdd.value ? 'api/v1/sysDept/save' : 'api/v1/sysDept/update',
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
      <ElFormItem prop="parentId" label="父级ID">
        <ElInput v-model.trim="entity.parentId" />
      </ElFormItem>
      <ElFormItem prop="ancestors" label="父级部门ID集合">
        <ElInput v-model.trim="entity.ancestors" />
      </ElFormItem>
      <ElFormItem prop="deptName" label="部门名称">
        <ElInput v-model.trim="entity.deptName" />
      </ElFormItem>
      <ElFormItem prop="deptCode" label="部门编码">
        <ElInput v-model.trim="entity.deptCode" />
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
