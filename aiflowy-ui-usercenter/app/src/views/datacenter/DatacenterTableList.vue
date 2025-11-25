<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';
import { router } from '#/router';
import { Delete, Edit, Plus, View } from '@element-plus/icons-vue';
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
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import DatacenterTableModal from './DatacenterTableModal.vue';

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  tableName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
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
          .get(`/api/v1/datacenterTable/removeTable?tableId=${row.id}`)
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
function toDetailPage(row: any) {
  router.push({
    name: 'TableDetail',
    query: {
      tableId: row.id,
    },
  });
}
</script>

<template>
  <div class="page-container">
    <DatacenterTableModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('datacenterTable.tableName')" prop="tableName">
        <ElInput
          v-model="formInline.tableName"
          :placeholder="$t('datacenterTable.tableName')"
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
        v-access:code="'/api/v1/datacenterTable/save'"
        @click="showDialog({})"
        type="primary"
      >
        <ElIcon class="mr-1">
          <Plus />
        </ElIcon>
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/datacenterTable/page"
      :page-size="10"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn
            prop="tableName"
            :label="$t('datacenterTable.tableName')"
          >
            <template #default="{ row }">
              {{ row.tableName }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="tableDesc"
            :label="$t('datacenterTable.tableDesc')"
          >
            <template #default="{ row }">
              {{ row.tableDesc }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" :label="$t('datacenterTable.created')">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="220">
            <template #default="{ row }">
              <div>
                <ElButton
                  v-access:code="'/api/v1/datacenterTable/query'"
                  @click="toDetailPage(row)"
                  link
                  type="primary"
                >
                  <ElIcon class="mr-1">
                    <View />
                  </ElIcon>
                  {{ $t('button.view') }}
                </ElButton>
                <ElButton
                  v-access:code="'/api/v1/datacenterTable/save'"
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
                  v-access:code="'/api/v1/datacenterTable/remove'"
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
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
