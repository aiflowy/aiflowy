<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import type { ActionButton } from '#/components/page/CardList.vue';

import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Notebook, Plus, Search } from '@element-plus/icons-vue';
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
import { tryit } from 'radash';

import { api } from '#/api/request';
import defaultIcon from '#/assets/ai/knowledge/book.svg';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardPage from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import PageSide from '#/components/page/PageSide.vue';
import DocumentCollectionModal from '#/views/ai/documentCollection/DocumentCollectionModal.vue';

const router = useRouter();
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
// 操作按钮配置
const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '/api/v1/documentCollection/save',
    onClick(row) {
      aiKnowledgeModalRef.value.openDialog(row);
    },
  },
  {
    icon: Notebook,
    text: $t('documentCollection.actions.knowledge'),
    className: '',
    permission: '/api/v1/documentCollection/save',
    onClick(row) {
      router.push({
        path: '/ai/documentCollection/document',
        query: {
          id: row.id,
          pageKey: '/ai/documentCollection',
        },
      });
    },
  },
  {
    icon: Search,
    text: $t('documentCollection.actions.retrieve'),
    className: '',
    permission: '',
    onClick(row) {
      router.push({
        path: '/ai/documentCollection/document',
        query: {
          id: row.id,
          pageKey: '/ai/documentCollection',
          activeMenu: 'knowledgeSearch',
        },
      });
    },
  },
  {
    text: $t('button.delete'),
    icon: Delete,
    className: 'item-danger',
    permission: '/api/v1/documentCollection/remove',
    onClick(row) {
      handleDelete(row);
    },
  },
];

onMounted(() => {
  getCategoryList();
});
const handleDelete = (item: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      api
        .post('/api/v1/documentCollection/remove', { id: item.id })
        .then((res) => {
          if (res.errorCode === 0) {
            ElMessage.success($t('message.deleteOkMessage'));
            pageDataRef.value.setQuery({});
          }
        });
    })
    .catch(() => {});
};

const pageDataRef = ref();
const aiKnowledgeModalRef = ref();
const headerButtons = [
  {
    key: 'add',
    text: $t('documentCollection.actions.addKnowledge'),
    icon: Plus,
    type: 'primary',
    data: { action: 'add' },
    permission: '/api/v1/documentCollection/save',
  },
];
const handleButtonClick = (event: any, _item: any) => {
  switch (event.key) {
    case 'add': {
      aiKnowledgeModalRef.value.openDialog({});
      break;
    }
  }
};
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
const handleSearch = (params: any) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
const formData = ref<any>({});
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
function showControlDialog(item: any) {
  formRef.value?.resetFields();
  formData.value = { ...item };
  dialogVisible.value = true;
}
const categoryList = ref<any[]>([]);
const getCategoryList = async () => {
  const [, res] = await tryit(api.get)(
    '/api/v1/documentCollectionCategory/list',
    {
      params: { sortKey: 'sortNo', sortType: 'asc' },
    },
  );

  if (res && res.errorCode === 0) {
    categoryList.value = [
      {
        id: '',
        categoryName: $t('common.allCategories'),
      },
      ...res.data,
    ];
  }
};
function removeCategory(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post('/api/v1/documentCollectionCategory/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              done();
              getCategoryList();
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
const saveLoading = ref(false);
function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (valid) {
      saveLoading.value = true;
      const url = formData.value.id
        ? '/api/v1/documentCollectionCategory/update'
        : '/api/v1/documentCollectionCategory/save';
      api.post(url, formData.value).then((res) => {
        saveLoading.value = false;
        if (res.errorCode === 0) {
          getCategoryList();
          ElMessage.success(res.message);
          dialogVisible.value = false;
        }
      });
    }
  });
}
function changeCategory(category: any) {
  pageDataRef.value.setQuery({ categoryId: category.id });
}
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <div class="knowledge-header">
      <HeaderSearch
        :buttons="headerButtons"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>
    <div class="flex max-h-[calc(100vh-191px)] flex-1 gap-6">
      <PageSide
        label-key="categoryName"
        value-key="id"
        :menus="categoryList"
        :control-btns="controlBtns"
        :footer-button="footerButton"
        @change="changeCategory"
      />
      <div class="h-full flex-1 overflow-auto">
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/documentCollection/page"
          :page-size="12"
          :page-sizes="[12, 24, 36, 48]"
          :init-query-params="{ status: 1 }"
        >
          <template #default="{ pageList }">
            <CardPage
              :default-icon="defaultIcon"
              title-key="title"
              avatar-key="icon"
              description-key="description"
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

    <!--    新增知识库模态框-->
    <DocumentCollectionModal ref="aiKnowledgeModalRef" @reload="handleSearch" />
  </div>
</template>

<style scoped>
h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
</style>
