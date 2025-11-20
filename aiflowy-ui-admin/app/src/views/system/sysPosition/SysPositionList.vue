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
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

import SysPositionModal from './SysPositionModal.vue';

onMounted(() => {
  initDict();
});

const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  positionName: '',
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
          .post('/api/v1/sysPosition/remove', { id: row.id })
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
</script>

<template>
  <div class="page-container">
    <SysPositionModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('sysPosition.positionName')" prop="positionName">
        <ElInput
          v-model="formInline.positionName"
          :placeholder="$t('sysPosition.positionName')"
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
        v-access:code="'/api/v1/sysPosition/save'"
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
      page-url="/api/v1/sysPosition/page"
      :page-size="10"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn
            prop="positionName"
            :label="$t('sysPosition.positionName')"
          >
            <template #default="{ row }">
              {{ row.positionName }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="positionCode"
            :label="$t('sysPosition.positionCode')"
          >
            <template #default="{ row }">
              {{ row.positionCode }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="sortNo" :label="$t('sysPosition.sortNo')">
            <template #default="{ row }">
              {{ row.sortNo }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="status" :label="$t('sysPosition.status')">
            <template #default="{ row }">
              {{ dictStore.getDictLabel('dataStatus', row.status) }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" :label="$t('sysPosition.created')">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="remark" :label="$t('sysPosition.remark')">
            <template #default="{ row }">
              {{ row.remark }}
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="150">
            <template #default="{ row }">
              <div>
                <ElButton
                  v-access:code="'/api/v1/sysPosition/save'"
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
                  v-access:code="'/api/v1/sysPosition/remove'"
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
