<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElSwitch,
  ElTable,
  ElTableColumn
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import McpModal from './McpModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  title: '',
});
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
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
          .post('/api/v1/mcp/remove', { id: row.id })
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
const handleUpdate = (row: any) => {
  api.post('/api/v1/mcp/update', { ...row }).then((res) => {
    if (res.errorCode === 0) {
      ElMessage.success(res.message);
    }
  });
};
</script>

<template>
  <div class="page-container">
    <McpModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('mcp.title')" prop="title">
        <ElInput v-model="formInline.title" :placeholder="$t('mcp.title')" />
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
      <ElButton @click="showDialog({ status: true })" type="primary">
        <ElIcon class="mr-1">
          <Plus />
        </ElIcon>
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <PageData ref="pageDataRef" page-url="/api/v1/mcp/page" :page-size="10">
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="title" :label="$t('mcp.title')">
            <template #default="{ row }">
              {{ row.title }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="description" :label="$t('mcp.description')">
            <template #default="{ row }">
              {{ row.description }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" :label="$t('mcp.created')">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="status" :label="$t('mcp.status')">
            <template #default="{ row }">
              <ElSwitch v-model="row.status" @change="handleUpdate(row)" />
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="150">
            <template #default="{ row }">
              <ElButton @click="showDialog(row)" link type="primary">
                <ElIcon class="mr-1">
                  <Edit />
                </ElIcon>
                {{ $t('button.edit') }}
              </ElButton>
              <ElButton @click="remove(row)" link type="danger">
                <ElIcon class="mr-1">
                  <Delete />
                </ElIcon>
                {{ $t('button.delete') }}
              </ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
