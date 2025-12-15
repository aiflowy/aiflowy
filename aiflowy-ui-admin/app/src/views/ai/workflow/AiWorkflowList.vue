<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import type { RequestResult } from '@aiflowy/types';

import type { ActionButton } from '#/components/page/CardList.vue';

import { computed, markRaw, onMounted, ref } from 'vue';

import { tryit } from '@aiflowy/utils';

import {
  CopyDocument,
  Delete,
  Download,
  Edit,
  Plus,
  VideoPlay,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
} from 'element-plus';

import { api } from '#/api/request';
import workflowIcon from '#/assets/ai/workflow/workflowIcon.png';
// import workflowSvg from '#/assets/workflow.svg';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import DesignIcon from '#/components/icons/DesignIcon.vue';
import CardList from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import PageSide from '#/components/page/PageSide.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import AiWorkflowModal from './AiWorkflowModal.vue';

interface FieldDefinition {
  // 字段名称
  prop: string;
  // 字段标签
  label: string;
  // 字段类型：input, number, select, radio, checkbox, switch, date, datetime
  type?: 'input' | 'number';
  // 是否必填
  required?: boolean;
  // 占位符
  placeholder?: string;
}

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
    icon: DesignIcon,
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
  getSideList();
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
const fieldDefinitions = ref<FieldDefinition[]>([
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

const formData = ref<any>({});
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const saveLoading = ref(false);
const sideList = ref<any[]>([]);
const controlBtns = [
  {
    icon: Edit,
    label: $t('button.edit'),
    onClick(row: any) {
      showControlDialog(row);
    },
  },
  {
    type: 'danger',
    icon: Delete,
    label: $t('button.delete'),
    onClick(row: any) {
      removeCategory(row);
    },
  },
];
const footerButton = {
  icon: Plus,
  label: $t('button.add'),
  onClick() {
    showControlDialog({});
  },
};

const formRules = computed(() => {
  const rules: Record<string, any[]> = {};
  fieldDefinitions.value.forEach((field) => {
    const fieldRules = [];
    if (field.required) {
      fieldRules.push({
        required: true,
        message: `${$t('message.required')}`,
        trigger: 'blur',
      });
    }
    if (fieldRules.length > 0) {
      rules[field.prop] = fieldRules;
    }
  });
  return rules;
});

function changeCategory(category: any) {
  pageDataRef.value.setQuery({ categoryId: category.id });
}
function showControlDialog(item: any) {
  formRef.value?.resetFields();
  formData.value = { ...item };
  dialogVisible.value = true;
}
function removeCategory(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/aiWorkflowCategory/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              done();
              getSideList();
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
function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (valid) {
      saveLoading.value = true;
      const url = formData.value.id
        ? '/api/v1/aiWorkflowCategory/update'
        : '/api/v1/aiWorkflowCategory/save';
      api.post(url, formData.value).then((res) => {
        saveLoading.value = false;
        if (res.errorCode === 0) {
          ElMessage.success(res.message);
          dialogVisible.value = false;
          getSideList();
        }
      });
    }
  });
}
const getSideList = async () => {
  const [, res] = await tryit<RequestResult>(
    api.get('/api/v1/aiWorkflowCategory/list', {
      params: { sortKey: 'sortNo', sortType: 'asc' },
    }),
  );

  if (res && res.errorCode === 0) {
    sideList.value = [
      {
        id: '',
        categoryName: $t('common.allCategories'),
      },
      ...res.data,
    ];
  }
};
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <AiWorkflowModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />
    <div class="flex flex-1 gap-2.5">
      <PageSide
        label-key="categoryName"
        value-key="id"
        :menus="sideList"
        :control-btns="controlBtns"
        :footer-button="footerButton"
        @change="changeCategory"
      />
      <div class="h-[calc(100vh-192px)] flex-1 overflow-auto">
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

    <ElDialog
      v-model="dialogVisible"
      :title="formData.id ? `${$t('button.edit')}` : `${$t('button.add')}`"
      :close-on-click-modal="false"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <!-- 动态生成表单项 -->
        <ElFormItem
          v-for="field in fieldDefinitions"
          :key="field.prop"
          :label="field.label"
          :prop="field.prop"
        >
          <ElInput
            v-if="!field.type || field.type === 'input'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
          />
          <ElInputNumber
            v-else-if="field.type === 'number'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            style="width: 100%"
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="dialogVisible = false">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton type="primary" @click="handleSubmit" :loading="saveLoading">
          {{ $t('button.confirm') }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped></style>
