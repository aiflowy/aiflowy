<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import SysLogModal from './SysLogModal.vue';

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  actionName: '',
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
</script>

<template>
  <div class="page-container">
    <SysLogModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('sysLog.actionName')" prop="actionName">
        <ElInput
          v-model="formInline.actionName"
          :placeholder="$t('sysLog.actionName')"
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
    <div class="handle-div"></div>
    <PageData ref="pageDataRef" page-url="/api/v1/sysLog/page" :page-size="10">
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="accountId" :label="$t('sysLog.accountId')">
            <template #default="{ row }">
              {{ row.accountId }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionName" :label="$t('sysLog.actionName')">
            <template #default="{ row }">
              {{ row.actionName }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionType" :label="$t('sysLog.actionType')">
            <template #default="{ row }">
              {{ row.actionType }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionClass" :label="$t('sysLog.actionClass')">
            <template #default="{ row }">
              {{ row.actionClass }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionMethod" :label="$t('sysLog.actionMethod')">
            <template #default="{ row }">
              {{ row.actionMethod }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionUrl" :label="$t('sysLog.actionUrl')">
            <template #default="{ row }">
              {{ row.actionUrl }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionIp" :label="$t('sysLog.actionIp')">
            <template #default="{ row }">
              {{ row.actionIp }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionParams" :label="$t('sysLog.actionParams')">
            <template #default="{ row }">
              {{ row.actionParams }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="actionBody" :label="$t('sysLog.actionBody')">
            <template #default="{ row }">
              {{ row.actionBody }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="status" :label="$t('sysLog.status')">
            <template #default="{ row }">
              {{ row.status }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" :label="$t('sysLog.created')">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
