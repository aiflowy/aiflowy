<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import { ElTable, ElTableColumn } from 'element-plus';

import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';

import SysLogModal from './SysLogModal.vue';

const pageDataRef = ref();
const saveDialog = ref();
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ actionName: params, isQueryOr: true });
};
function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <SysLogModal ref="saveDialog" @reload="reset" />
    <HeaderSearch @search="handleSearch" />

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysLog/page"
        :page-size="10"
      >
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
            <ElTableColumn
              prop="actionMethod"
              :label="$t('sysLog.actionMethod')"
            >
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
            <ElTableColumn
              prop="actionParams"
              :label="$t('sysLog.actionParams')"
            >
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
  </div>
</template>

<style scoped></style>
