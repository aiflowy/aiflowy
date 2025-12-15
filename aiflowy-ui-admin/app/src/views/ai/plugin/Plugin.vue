<script setup>
import { markRaw, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
} from 'element-plus';

import { api } from '#/api/request';
// import CategoryCrudPanel from '#/components/categoryPanel/CategoryCrudPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CategorizeIcon from '#/components/icons/CategorizeIcon.vue';
import PluginToolIcon from '#/components/icons/PluginToolIcon.vue';
import CardPage from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import PageSide from '#/components/page/PageSide.vue';
import AddPluginModal from '#/views/ai/plugin/AddPluginModal.vue';
import CategoryPluginModal from '#/views/ai/plugin/CategoryPluginModal.vue';

const router = useRouter();
// 操作按钮配置
const actions = ref([
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '/api/v1/aiPlugin/save',
    onClick(item) {
      aiPluginModalRef.value.openDialog(item);
    },
  },
  {
    icon: PluginToolIcon,
    text: $t('plugin.button.tools'),
    className: '',
    permission: '/api/v1/aiPlugin/save',
    onClick(item) {
      router.push({
        path: '/ai/plugin/tools',
        query: {
          id: item.id,
          pageKey: '/ai/plugin',
        },
      });
    },
  },
  {
    icon: CategorizeIcon,
    text: $t('plugin.button.categorize'),
    className: '',
    permission: '/api/v1/aiPlugin/save',
    onClick(item) {
      categoryCategoryModal.value.openDialog(item);
    },
  },
  {
    icon: Delete,
    text: $t('button.delete'),
    className: '',
    permission: '/api/v1/aiPlugin/remove',
    onClick(item) {
      handleDelete(item);
    },
  },
]);
const categoryList = ref([]);
const controlBtns = [
  {
    icon: Edit,
    label: $t('button.edit'),
    onClick(row) {
      isEdit.value = true;
      dialogVisible.value = true;
    },
  },
  {
    type: 'danger',
    icon: Delete,
    label: $t('button.delete'),
    onClick(row) {
      handleDeleteCategory(row);
    },
  },
];
const footerButton = {
  icon: Plus,
  label: $t('button.add'),
  onClick() {
    handleAddCategory();
  },
};
const getPluginCategoryList = async () => {
  return api.get('/api/v1/aiPluginCategories/list').then((res) => {
    if (res.errorCode === 0) {
      categoryList.value = [
        { id: '0', name: $t('common.allCategories') },
        ...res.data,
      ];
    }
  });
};
onMounted(() => {
  getPluginCategoryList();
});
const handleDelete = (item) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      api
        .post('/api/v1/aiPlugin/plugin/remove', { id: item.id })
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
const aiPluginModalRef = ref();
const categoryCategoryModal = ref();
const headerButtons = [
  {
    key: 'add',
    text: $t('plugin.button.addPlugin'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'add' },
  },
];
const pluginCategoryId = ref('0');
const dialogVisible = ref(false); // 弹窗显隐
const isEdit = ref(false); // 是否为编辑模式
const formData = ref({ name: '' });

/**
 * 编辑按钮点击事件
 * @param {object} row - 表格当前行数据
 */
const handleEdit = (row) => {
  isEdit.value = true;
  // 使用提取原始数据的方法
  formData.value = extractFromProxy(row);
  dialogVisible.value = true;
};
const handleSubmit = () => {
  // 触发对应事件，传递表单数据
  if (isEdit.value) {
    handleEditCategory(formData.value);
  } else {
    handleAddCategory(formData.value);
  }
  // 提交后关闭弹窗
  dialogVisible.value = false;
};
const handleButtonClick = (event, _item) => {
  switch (event.key) {
    case 'add': {
      aiPluginModalRef.value.openDialog({});
      break;
    }
  }
};
const handleSearch = (params) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
const handleEditCategory = (params) => {
  api
    .post('/api/v1/aiPluginCategories/update', {
      id: params.id,
      name: params.name,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        getPluginCategoryList();
        ElMessage.success($t('message.updateOkMessage'));
      }
    });
};
const handleAddCategory = (params) => {
  api
    .post('/api/v1/aiPluginCategories/save', { name: params.name })
    .then((res) => {
      if (res.errorCode === 0) {
        getPluginCategoryList();
        ElMessage.success($t('message.saveOkMessage'));
      }
    });
};
const handleDeleteCategory = (params) => {
  api
    .get(`/api/v1/aiPluginCategories/doRemoveCategory?id=${params.id}`)
    .then((res) => {
      if (res.errorCode === 0) {
        getPluginCategoryList();
        ElMessage.success($t('message.deleteOkMessage'));
      }
    });
};
const handleClickCategory = (item) => {
  pageDataRef.value.setQuery({ category: item.id });
};
</script>

<template>
  <div class="knowledge-container">
    <div class="knowledge-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>
    <div class="plugin-content-container">
      <div class="category-panel-container">
        <!-- <CategoryCrudPanel
          :title="$t('plugin.pluginCategory')"
          title-key="name"
          :panel-width="220"
          default-selected-category="0"
          :category-data="categoryList"
          @edit="handleEditCategory"
          @add="handleAddCategory"
          @delete="handleDeleteCategory"
          @click="handleClickCategory"
          value-key="id"
          :default-form-data="{ name: '' }"
        /> -->
        <PageSide
          :title="$t('plugin.pluginCategory')"
          label-key="name"
          value-key="id"
          :menus="categoryList"
          :control-btns="controlBtns"
          :footer-button="footerButton"
          @change="handleClickCategory"
        />
      </div>

      <div
        class="plugin-content-data-container h-[calc(100vh-185px)] overflow-auto"
      >
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/aiPlugin/pageByCategory"
          :page-size="12"
          :page-sizes="[12, 24, 36, 48]"
          :extra-query-params="{ category: pluginCategoryId }"
        >
          <template #default="{ pageList }">
            <CardPage
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
    <AddPluginModal ref="aiPluginModalRef" @reload="handleSearch" />
    <CategoryPluginModal ref="categoryCategoryModal" @reload="handleSearch" />
    <ElDialog
      :title="
        isEdit ? `${$t('button.edit')}${title}` : `${$t('button.add')}${title}`
      "
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm :model="formData" status-icon>
        <ElFormItem :prop="titleKey">
          <ElInput v-model.trim="formData[titleKey]" />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="dialogVisible = false">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton type="primary" @click="handleSubmit">
          {{ $t('button.confirm') }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
.knowledge-container {
  padding: 24px;
  width: 100%;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.plugin-content-container {
  display: flex;
  gap: 24px;
  height: calc(100vh - 161px);
  padding-top: 24px;
}

.plugin-content-data-container {
  /* padding: 20px; */
  /* background-color: var(--el-bg-color); */
  width: 100%;
  border-top-right-radius: var(--el-border-radius-base);
  border-bottom-right-radius: var(--el-border-radius-base);
}
</style>
