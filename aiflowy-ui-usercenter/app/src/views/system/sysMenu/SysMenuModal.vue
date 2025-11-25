<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { IconPicker } from '@aiflowy/common-ui';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import { $t } from '#/locales';
import { componentKeys } from '#/router/routes';

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
  menuType: '',
  menuTitle: '',
  menuUrl: '',
  component: '',
  menuIcon: '',
  isShow: '',
  permissionTag: '',
  sortNo: '',
  status: '',
  remark: '',
});
const btnLoading = ref(false);
const rules = ref({
  parentId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  menuType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  menuTitle: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  isShow: [
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
          isAdd.value ? 'api/v1/sysMenu/save' : 'api/v1/sysMenu/update',
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
    :z-index="1999"
  >
    <ElForm
      label-width="120px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="parentId" :label="$t('sysMenu.parentId')">
        <DictSelect
          :extra-options="[{ label: $t('sysMenu.root'), value: 0 }]"
          v-model="entity.parentId"
          dict-code="sysMenu"
        />
      </ElFormItem>
      <ElFormItem prop="menuType" :label="$t('sysMenu.menuType')">
        <DictSelect v-model="entity.menuType" dict-code="menuType" />
      </ElFormItem>
      <ElFormItem prop="menuTitle" :label="$t('sysMenu.menuTitle')">
        <ElInput v-model.trim="entity.menuTitle" />
      </ElFormItem>
      <ElFormItem prop="menuUrl" :label="$t('sysMenu.menuUrl')">
        <ElInput v-model.trim="entity.menuUrl" />
      </ElFormItem>
      <ElFormItem prop="component" :label="$t('sysMenu.component')">
        <ElInput v-model.trim="entity.component">
          <template #append>
            <ElSelect
              v-model="entity.component"
              filterable
              style="width: 100px"
            >
              <ElOption
                v-for="item in componentKeys"
                :key="item"
                :label="item"
                :value="item"
              />
            </ElSelect>
          </template>
        </ElInput>
      </ElFormItem>
      <ElFormItem prop="menuIcon" :label="$t('sysMenu.menuIcon')">
        <IconPicker v-model="entity.menuIcon" />
      </ElFormItem>
      <ElFormItem prop="isShow" :label="$t('sysMenu.isShow')">
        <DictSelect v-model="entity.isShow" dict-code="yesOrNo" />
      </ElFormItem>
      <ElFormItem prop="permissionTag" :label="$t('sysMenu.permissionTag')">
        <ElInput v-model.trim="entity.permissionTag" />
      </ElFormItem>
      <ElFormItem prop="sortNo" :label="$t('sysMenu.sortNo')">
        <ElInput v-model.trim="entity.sortNo" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysMenu.remark')">
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
