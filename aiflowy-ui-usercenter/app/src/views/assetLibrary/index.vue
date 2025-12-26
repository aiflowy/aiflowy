<script setup lang="ts">
import { ref } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';
import { cn } from '@aiflowy/utils';

import { Delete, EditPen, Plus, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElContainer,
  ElHeader,
  ElMain,
  ElMessage,
  ElMessageBox,
  ElSpace,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import AiResourceModal from '#/views/ai/resource/AiResourceModal.vue';
import PreviewModal from '#/views/ai/resource/PreviewModal.vue';

import Grid from './grid.vue';
import List from './list.vue';

type ViewType = 'grid' | 'list';
export interface Asset {
  id: number;
  name: string;
  source: string;
  type: string;
  size: string;
  lastUpdateTime: string;
}

const viewType = ref<ViewType>('list');
const pageDataRef = ref();
const checkedItems = ref<any[]>([]);
const saveDialog = ref();
const previewDialog = ref();
function setCheckedItem(items: any[]) {
  checkedItems.value = items;
}
function reset() {
  pageDataRef.value.setQuery({});
}
function preview(row: any) {
  previewDialog.value.openDialog({ ...row });
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
}
function download(row: any) {
  window.open(row.resourceUrl, '_blank');
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
          .post('/userCenter/resource/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset();
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
function batchRemove() {
  const ids = checkedItems.value.map((item) => item.id);
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/userCenter/resource/removeBatch', { ids })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset();
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
function handleOperation(type: string) {
  if (checkedItems.value.length > 1 || checkedItems.value.length === 0) {
    ElMessage.warning('只能操作一项数据');
    return;
  }
  switch (type) {
    case 'edit': {
      showDialog(checkedItems.value[0]);
      break;
    }
    case 'preview': {
      preview(checkedItems.value[0]);
      break;
    }
    default: {
      break;
    }
  }
}
</script>

<template>
  <ElContainer class="bg-background-deep h-full">
    <ElHeader class="flex flex-col gap-6 !p-8 !pb-0" height="auto">
      <ElSpace :size="24">
        <h1 class="text-2xl font-medium">素材库</h1>
        <!--<ElSpace
          class="rounded-lg border border-[#E6E9EE] bg-[#F8FBFE] px-3.5 py-2.5"
        >
          <span class="text-sm font-medium text-[#969799]">
            <span class="text-[#1A1A1A]">256G</span> / 1T
          </span>
          <ElProgress
            class="w-[132px]"
            :percentage="20"
            :stroke-width="4"
            :show-text="false"
          />
        </ElSpace>-->
      </ElSpace>
      <div class="flex w-full items-center justify-between">
        <ElSpace class="text-2xl text-[#969799]">
          <IconifyIcon
            icon="svg:list"
            :class="
              cn(
                'h-8 w-8 cursor-pointer text-[#969799]',
                viewType === 'list'
                  ? 'text-primary'
                  : 'hover:text-foreground dark:hover:text-accent',
              )
            "
            @click="viewType = 'list'"
          />
          <IconifyIcon
            icon="svg:grid"
            :class="
              cn(
                'h-8 w-8 cursor-pointer text-[#969799]',
                viewType === 'grid'
                  ? 'text-primary'
                  : 'hover:text-foreground dark:hover:text-accent',
              )
            "
            @click="viewType = 'grid'"
          />
        </ElSpace>
        <div class="flex items-center gap-2.5">
          <div
            v-if="checkedItems.length > 0"
            class="border-border bg-background flex items-center rounded border px-2 py-1.5"
          >
            <ElButton
              class="[--el-font-weight-primary:400]"
              link
              :icon="View"
              @click="handleOperation('preview')"
            >
              预览
            </ElButton>
            <ElButton
              class="[--el-font-weight-primary:400]"
              link
              :icon="EditPen"
              @click="handleOperation('edit')"
            >
              编辑
            </ElButton>
            <ElButton
              class="[--el-font-weight-primary:400]"
              link
              :icon="Delete"
              @click="batchRemove"
            >
              删除
            </ElButton>
          </div>
          <ElButton type="primary" :icon="Plus" @click="showDialog({})">
            本地上传
          </ElButton>
        </div>
      </div>
    </ElHeader>
    <ElMain class="!px-8 !py-6">
      <PageData
        ref="pageDataRef"
        page-url="/userCenter/resource/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <div class="flex flex-col items-center gap-5">
            <List
              :on-checked-change="setCheckedItem"
              v-show="viewType === 'list'"
              :data="pageList"
              :on-download="download"
              :on-edit="showDialog"
              :on-preview="preview"
              :on-remove="remove"
            />
            <Grid
              :on-checked-change="setCheckedItem"
              v-show="viewType === 'grid'"
              :data="pageList"
            />
          </div>
        </template>
      </PageData>
    </ElMain>
    <PreviewModal ref="previewDialog" />
    <AiResourceModal ref="saveDialog" @reload="reset" />
  </ElContainer>
</template>
