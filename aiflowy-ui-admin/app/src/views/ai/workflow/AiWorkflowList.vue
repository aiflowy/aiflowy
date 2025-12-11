<script setup lang="ts">
import type { ActionButton } from '#/components/page/CardList.vue';

import { markRaw, onMounted, ref } from 'vue';

import {
  CopyDocument,
  Delete,
  Download,
  Edit,
  Plus,
  Tools,
  VideoPlay,
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import { api } from '#/api/request';
import workflowIcon from '#/assets/ai/workflow/workflowIcon.png';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardList from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import PageSide from '#/components/page/PageSide.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import AiWorkflowModal from './AiWorkflowModal.vue';

const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '',
    onClick: (row: any) => {
      showDialog(row);
    },
  },
  {
    icon: Tools,
    text: $t('button.design'),
    className: '',
    permission: '',
    onClick: (row: any) => {
      toDesignPage(row);
    },
  },
  {
    icon: VideoPlay,
    text: $t('button.run'),
    className: '',
    permission: '',
    onClick: (row: any) => {
      router.push({
        name: 'RunPage',
        query: {
          id: row.id,
        },
      });
    },
  },
  {
    icon: Download,
    text: $t('button.export'),
    className: '',
    permission: '',
    onClick: (row: any) => {
      exportJson(row);
    },
  },
  {
    icon: CopyDocument,
    text: $t('button.copy'),
    className: '',
    permission: '',
    onClick: (row: any) => {
      showDialog({
        title: `${row.title}Copy`,
        content: row.content,
      });
    },
  },
  {
    icon: Delete,
    text: $t('button.delete'),
    className: 'item-danger',
    permission: '',
    onClick: (row: any) => {
      remove(row);
    },
  },
];
onMounted(() => {
  initDict();
});
const pageDataRef = ref();
const saveDialog = ref();
const dictStore = useDictStore();
const headerButtons = [
  {
    key: 'create',
    text: $t('button.add'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/aiWorkflow/save',
  },
];

function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
function reset() {
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
          .post('/api/v1/aiWorkflow/remove', { id: row.id })
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
function toDesignPage(row: any) {
  router.push({
    name: 'WorkflowDesign',
    query: {
      id: row.id,
    },
  });
}
function exportJson(row: any) {
  api
    .get('/api/v1/aiWorkflow/exportWorkFlow', {
      params: {
        id: row.id,
      },
    })
    .then((res) => {
      const text = res.data;
      const element = document.createElement('a');
      element.setAttribute(
        'href',
        `data:text/plain;charset=utf-8,${encodeURIComponent(text)}`,
      );
      element.setAttribute('download', `${row.title}.json`);
      element.style.display = 'none';
      document.body.append(element);
      element.click();
      element.remove();
      ElMessage.success($t('message.downloadSuccess'));
    });
}
const fieldDefinitions = ref<any>([
  {
    prop: 'categoryName',
    label: $t('aiWorkflowCategory.categoryName'),
    type: 'input',
    required: true,
    placeholder: $t('aiWorkflowCategory.categoryName'),
  },
  {
    prop: 'sortNo',
    label: $t('aiWorkflowCategory.sortNo'),
    type: 'number',
    required: false,
    placeholder: $t('aiWorkflowCategory.sortNo'),
  },
]);
function changeCategory(categoryId: any) {
  pageDataRef.value.setQuery({ categoryId });
}
</script>

<template>
  <div class="space-y-6 p-6">
    <AiWorkflowModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />
    <div class="flex gap-2.5">
      <PageSide
        list-url="/api/v1/aiWorkflowCategory/list"
        save-url="/api/v1/aiWorkflowCategory/save"
        update-url="/api/v1/aiWorkflowCategory/update"
        delete-url="/api/v1/aiWorkflowCategory/remove"
        :fields="fieldDefinitions"
        name-key="categoryName"
        @on-selected="changeCategory"
        :extra-query-params="{
          sortKey: 'sortNo',
          sortType: 'asc',
        }"
      />
      <div class="flex-auto">
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/aiWorkflow/page"
          :page-sizes="[12, 18, 24]"
          :page-size="12"
        >
          <template #default="{ pageList }">
            <CardList
              :default-icon="workflowIcon"
              :data="pageList"
              :actions="actions"
            />
          </template>
        </PageData>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
