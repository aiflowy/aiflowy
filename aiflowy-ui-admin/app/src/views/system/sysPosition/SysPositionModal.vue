<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);

defineExpose({
  openDialog,
});

const saveForm = ref<FormInstance>();
const dialogVisible = ref(false);
const isAdd = ref(true);
const entity = ref<any>({
  positionName: '',
  positionCode: '',
  sortNo: 0,
  status: 1,
  remark: '',
});
const btnLoading = ref(false);

const rules = ref({
  positionName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  positionCode: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  status: [
    { required: true, message: $t('message.required'), trigger: 'change' },
  ],
});

function openDialog(row: any) {
  if (row.id) {
    isAdd.value = false;
    entity.value = { ...row };
  } else {
    isAdd.value = true;
    entity.value = {
      sortNo: 0,
      status: 1,
    };
  }
  dialogVisible.value = true;
}

function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      const url = isAdd.value ? '/api/v1/sysPosition/save' : '/api/v1/sysPosition/update';
      api
        .post(url, entity.value)
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
    width="500px"
  >
    <ElForm
      label-width="100px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="positionName" :label="$t('sysPosition.positionName') || '岗位名称'">
        <ElInput v-model.trim="entity.positionName" placeholder="请输入岗位名称" />
      </ElFormItem>
      <ElFormItem prop="positionCode" :label="$t('sysPosition.positionCode') || '岗位编码'">
        <ElInput v-model.trim="entity.positionCode" placeholder="请输入岗位编码" />
      </ElFormItem>
      <ElFormItem prop="sortNo" :label="$t('sysPosition.sortNo') || '排序'">
        <ElInputNumber v-model="entity.sortNo" :min="0" :max="999" style="width: 100%" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('sysPosition.status') || '状态'">
        <DictSelect v-model="entity.status" dict-code="dataStatus" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysPosition.remark')">
        <ElInput v-model.trim="entity.remark" type="textarea" :rows="3" />
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
