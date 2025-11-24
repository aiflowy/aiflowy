<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { formatBytes } from '@aiflowy/utils';

import { Delete, Edit, Plus, View, Download } from '@element-plus/icons-vue';
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

import AiResourceModal from './AiResourceModal.vue';
import PreviewModal from "#/views/ai/resource/PreviewModal.vue";

onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const previewDialog = ref();
const formInline = ref({
  resourceName: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('resourceType');
  dictStore.fetchDictionary('resourceOriginType');
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
          .post('/api/v1/aiResource/remove', { id: row.id })
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
function preview(row: any) {
  previewDialog.value.openDialog({ ...row });
}
function download(row: any) {
  window.open(row.resourceUrl, '_blank');
}
</script>

<template>
  <div class="page-container">
    <PreviewModal ref="previewDialog" />
    <AiResourceModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('aiResource.resourceName')" prop="resourceName">
        <ElInput
          v-model="formInline.resourceName"
          :placeholder="$t('aiResource.resourceName')"
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
        v-access:code="'/api/v1/aiResource/save'"
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
      page-url="/api/v1/aiResource/page"
      :page-size="10"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn
            prop="resourceType"
            :label="$t('aiResource.resourceType')"
          >
            <template #default="{ row }">
              {{ dictStore.getDictLabel('resourceType', row.resourceType) }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="resourceName"
            :label="$t('aiResource.resourceName')"
          >
            <template #default="{ row }">
              {{ row.resourceName }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="suffix" :label="$t('aiResource.suffix')">
            <template #default="{ row }">
              {{ row.suffix }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="fileSize" :label="$t('aiResource.fileSize')">
            <template #default="{ row }">
              {{ formatBytes(row.fileSize) }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="origin" :label="$t('aiResource.origin')">
            <template #default="{ row }">
              {{ dictStore.getDictLabel('resourceOriginType', row.origin) }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="created" :label="$t('aiResource.created')">
            <template #default="{ row }">
              {{ row.created }}
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="150">
            <template #default="{ row }">
              <div>
                <ElButton @click="preview(row)" link type="primary">
                  <ElIcon class="mr-1">
                    <View />
                  </ElIcon>
                  {{ $t('button.preview') }}
                </ElButton>
                <ElButton @click="download(row)" link type="primary">
                  <ElIcon class="mr-1">
                    <Download />
                  </ElIcon>
                  {{ $t('button.download') }}
                </ElButton>
                <ElButton
                  v-access:code="'/api/v1/aiResource/save'"
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
                  v-access:code="'/api/v1/aiResource/remove'"
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
