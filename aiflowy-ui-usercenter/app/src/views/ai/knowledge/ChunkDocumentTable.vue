<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Delete, Edit } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
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

const props = defineProps({
  documentId: {
    type: String,
    default: '',
  },
});
const dialogVisible = ref(false);
const pageDataRef = ref();
const handleEdit = (row: any) => {
  form.value = { id: row.id, content: row.content };
  openDialog();
};
const handleDelete = (row: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      btnLoading.value = true;
      api
        .post('/api/v1/aiDocumentChunk/removeChunk', { id: row.id })
        .then((res: any) => {
          btnLoading.value = false;
          if (res.errorCode !== 0) {
            ElMessage.error(res.message);
            return;
          }
          ElMessage.success($t('message.deleteOkMessage'));
          pageDataRef.value.setQuery(queryParams);
        });
    })
    .catch(() => {});
};
const openDialog = () => {
  dialogVisible.value = true;
};
const closeDialog = () => {
  dialogVisible.value = false;
};
const queryParams = ref({
  documentId: props.documentId,
  sortKey: 'sorting',
  sortType: 'asc',
});
const save = () => {
  btnLoading.value = true;
  api.post('/api/v1/aiDocumentChunk/update', form.value).then((res: any) => {
    btnLoading.value = false;
    if (res.errorCode !== 0) {
      ElMessage.error(res.message);
      return;
    }
    ElMessage.success($t('message.updateOkMessage'));
    pageDataRef.value.setQuery(queryParams);
    closeDialog();
  });
};
const btnLoading = ref(false);
const basicFormRef = ref();
const form = ref({
  id: '',
  content: '',
});
</script>

<template>
  <div>
    <PageData
      page-url="/api/v1/aiDocumentChunk/page"
      ref="pageDataRef"
      :page-size="10"
      :extra-query-params="queryParams"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" style="width: 100%" size="large">
          <ElTableColumn
            prop="sorting"
            :label="$t('aiKnowledge.sorting')"
            width="180"
          />
          <ElTableColumn
            prop="content"
            :label="$t('aiKnowledge.content')"
            min-width="240"
          />
          <ElTableColumn fixed="right" :label="$t('common.handle')">
            <template #default="scope">
              <ElButton link type="primary" @click="handleEdit(scope.row)">
                <ElIcon class="mr-1">
                  <Edit />
                </ElIcon>
                {{ $t('button.edit') }}
              </ElButton>
              <ElButton link type="danger" @click="handleDelete(scope.row)">
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
    <ElDialog v-model="dialogVisible" :title="$t('button.edit')" width="50%">
      <ElForm
        ref="basicFormRef"
        style="width: 100%; margin-top: 20px"
        :model="form"
      >
        <ElFormItem>
          <ElInput v-model="form.content" :rows="20" type="textarea" />
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
  </div>
</template>

<style scoped></style>
