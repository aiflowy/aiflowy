<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';
import { downloadFileFromBlob } from '@aiflowy/utils';

import { Delete, Download, MoreFilled } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElImage,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import documentIcon from '#/assets/ai/knowledge/document.svg';
import PageData from '#/components/page/PageData.vue';

const props = defineProps({
  knowledgeId: {
    required: true,
    type: String,
  },
});

const emits = defineEmits(['viewDoc']);
const pageDataRef = ref();
const handleView = (row: any) => {
  emits('viewDoc', row.id);
};
const handleDownload = (row: any) => {
  api.download(row.documentPath).then((res) => {
    downloadFileFromBlob({
      fileName: `${row.title}.${row.documentType}`,
      source: res,
    });
  });
};
const handleDelete = (row: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('button.confirm'),
    cancelButtonText: $t('button.cancel'),
    type: 'warning',
  }).then(() => {
    api.post('/api/v1/document/removeDoc', { id: row.id }).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        pageDataRef.value.setQuery({ id: props.knowledgeId });
      }
    });
    // 删除逻辑
  });
};
</script>

<template>
  <PageData
    page-url="/api/v1/document/documentList"
    ref="pageDataRef"
    :page-size="10"
    :extra-query-params="{
      id: props.knowledgeId,
      sort: 'desc',
      sortKey: 'created',
    }"
  >
    <template #default="{ pageList }">
      <ElTable :data="pageList" style="width: 100%" size="large">
        <ElTableColumn
          prop="fileName"
          :label="$t('documentCollection.fileName')"
        >
          <template #default="{ row }">
            <span class="file-name-container">
              <ElImage :src="documentIcon" class="mr-1" />
              <span class="title">
                {{ row.title }}.{{ row.documentType }}
              </span>
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn
          prop="documentType"
          :label="$t('documentCollection.documentType')"
          width="180"
        />
        <ElTableColumn
          prop="chunkCount"
          :label="$t('documentCollection.knowledgeCount')"
          width="180"
        />
        <ElTableColumn
          :label="$t('documentCollection.createdModifyTime')"
          width="200"
        >
          <template #default="{ row }">
            <div class="time-container">
              <span>{{ row.created }}</span>
              <span>{{ row.modified }}</span>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn :label="$t('common.handle')" width="100" align="right">
          <template #default="{ row }">
            <div class="flex items-center gap-3">
              <ElButton link type="primary" @click="handleView(row)">
                {{ $t('button.view') }}
              </ElButton>

              <ElDropdown>
                <ElButton link :icon="MoreFilled" />

                <template #dropdown>
                  <ElDropdownMenu>
                    <ElDropdownItem @click="handleDownload(row)">
                      <ElButton link :icon="Download">
                        {{ $t('button.download') }}
                      </ElButton>
                    </ElDropdownItem>
                    <ElDropdownItem @click="handleDelete(row)">
                      <ElButton link :icon="Delete" type="danger">
                        {{ $t('button.delete') }}
                      </ElButton>
                    </ElDropdownItem>
                  </ElDropdownMenu>
                </template>
              </ElDropdown>
            </div>
          </template>
        </ElTableColumn>
      </ElTable>
    </template>
  </PageData>
</template>

<style scoped>
.time-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.file-name-container {
  display: flex;
  align-items: center;
}
.title {
  font-weight: 500;
  font-size: 14px;
  color: #1a1a1a;
  line-height: 20px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}
</style>
