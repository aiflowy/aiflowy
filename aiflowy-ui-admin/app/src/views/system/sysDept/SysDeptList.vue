<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import SysDeptModal from './SysDeptModal.vue';

onMounted(() => {
  getTree();
  initDict();
});

const formRef = ref<FormInstance>();
const treeData = ref([]);
const loading = ref(false);
const saveDialog = ref();
const formInline = ref({
  deptName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      getTree();
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
  getTree();
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/sysDept/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset(formRef.value);
              done();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
function getTree() {
  loading.value = true;
  api
    .get('/api/v1/sysDept/list', {
      params: {
        asTree: true,
        ...formInline.value,
      },
    })
    .then((res) => {
      loading.value = false;
      treeData.value = res.data;
    });
}
</script>

<template>
  <div class="page-container">
    <SysDeptModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('sysDept.deptName')" prop="deptName">
        <ElInput
          v-model="formInline.deptName"
          :placeholder="$t('sysDept.deptName')"
        />
      </ElFormItem>
      <ElFormItem>
        <ElButton @click="search(formRef)" type="primary">
          {{ $t('button.query') }}
        </ElButton>
        <ElButton @click="reset(formRef)">
          {{ $t('button.reset') }}
        </ElButton>
      </ElFormItem>
    </ElForm>
    <div class="handle-div">
      <ElButton
        v-access:code="'/api/v1/sysDept/save'"
        @click="showDialog({})"
        type="primary"
      >
        <ElIcon class="mr-1">
          <Plus />
        </ElIcon>
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <ElTable :data="treeData" row-key="id" v-loading="loading" border>
      <ElTableColumn width="50" />
      <ElTableColumn prop="deptName" :label="$t('sysDept.deptName')">
        <template #default="{ row }">
          {{ row.deptName }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="deptCode" :label="$t('sysDept.deptCode')">
        <template #default="{ row }">
          {{ row.deptCode }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="sortNo" :label="$t('sysDept.sortNo')">
        <template #default="{ row }">
          {{ row.sortNo }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="created" :label="$t('sysDept.created')">
        <template #default="{ row }">
          {{ row.created }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="remark" :label="$t('sysDept.remark')">
        <template #default="{ row }">
          {{ row.remark }}
        </template>
      </ElTableColumn>
      <ElTableColumn :label="$t('common.handle')" width="150">
        <template #default="{ row }">
          <div>
            <ElButton
              v-access:code="'/api/v1/sysDept/save'"
              @click="showDialog(row)"
              link
              type="primary"
            >
              <ElIcon class="mr-1">
                <Edit />
              </ElIcon>
              {{ $t('button.edit') }}
            </ElButton>
            <ElButton
              v-access:code="'/api/v1/sysDept/remove'"
              @click="remove(row)"
              link
              type="danger"
            >
              <ElIcon class="mr-1">
                <Delete />
              </ElIcon>
              {{ $t('button.delete') }}
            </ElButton>
          </div>
        </template>
      </ElTableColumn>
    </ElTable>
  </div>
</template>

<style scoped></style>
