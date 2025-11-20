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
  ElSwitch,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import Tree from '#/components/tree/Tree.vue';
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
  roleName: '',
  roleKey: '',
  status: '',
  remark: '',
});
const btnLoading = ref(false);
const rules = ref({
  roleName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  roleKey: [
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
    getMenuIds(row.id);
    getDeptIds(row.id);
  }
  entity.value = row;
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      api
        .post('/api/v1/sysRole/saveRole', entity.value)
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
function getMenuIds(roleId: any) {
  api.get(`/api/v1/sysRole/getRoleMenuIds?roleId=${roleId}`).then((res) => {
    entity.value.menuIds = res.data;
  });
}
function getDeptIds(roleId: any) {
  api.get(`/api/v1/sysRole/getRoleDeptIds?roleId=${roleId}`).then((res) => {
    entity.value.deptIds = res.data;
  });
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
      <ElFormItem prop="roleName" :label="$t('sysRole.roleName')">
        <ElInput v-model.trim="entity.roleName" />
      </ElFormItem>
      <ElFormItem prop="roleKey" :label="$t('sysRole.roleKey')">
        <ElInput v-model.trim="entity.roleKey" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('sysRole.status')">
        <DictSelect v-model="entity.status" dict-code="dataStatus" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysRole.remark')">
        <ElInput v-model.trim="entity.remark" />
      </ElFormItem>
      <ElFormItem :label="$t('sysRole.menuPermission')">
        <ElSwitch
          v-model="entity.menuCheckStrictly"
          :active-text="$t('sysRole.checkStrictlyTrue')"
          :inactive-text="$t('sysRole.checkStrictlyFalse')"
        />
        <Tree
          data-url="/api/v1/sysMenu/list?asTree=true"
          v-model="entity.menuIds"
          :default-props="{
            label: 'menuTitle',
            children: 'children',
          }"
          :check-strictly="!entity.menuCheckStrictly"
        />
      </ElFormItem>
      <ElFormItem :label="$t('sysRole.dataPermission')">
        <DictSelect v-model="entity.dataScope" dict-code="dataScope" />
        <div v-if="entity.dataScope === 5" style="width: 100%">
          <ElSwitch
            v-model="entity.deptCheckStrictly"
            :active-text="$t('sysRole.checkStrictlyTrue')"
            :inactive-text="$t('sysRole.checkStrictlyFalse')"
          />
          <Tree
            data-url="/api/v1/sysDept/list?asTree=true"
            v-model="entity.deptIds"
            :default-props="{
              label: 'deptName',
              children: 'children',
            }"
            :check-strictly="!entity.deptCheckStrictly"
          />
        </div>
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
