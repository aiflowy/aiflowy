<script setup>
import { markRaw, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import { api } from '#/api/request';
import CardPage from '#/components/cardPage/CardPage.vue';
import CategoryCrudPanel from '#/components/categoryPanel/CategoryCrudPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CategorizeIcon from '#/components/icons/CategorizeIcon.vue';
import PluginToolIcon from '#/components/icons/PluginToolIcon.vue';
import PageData from '#/components/page/PageData.vue';
import AddPluginModal from '#/views/ai/plugin/AddPluginModal.vue';
import CategoryPluginModal from '#/views/ai/plugin/CategoryPluginModal.vue';

const router = useRouter();
// 操作按钮配置
const actions = ref([
  {
    name: 'edit',
    label: $t('button.edit'),
    type: 'primary',
    icon: markRaw(Edit),
    permission: '/api/v1/aiPlugin/save',
  },
  {
    name: 'tools',
    label: $t('plugin.button.tools'),
    type: 'success',
    icon: markRaw(PluginToolIcon),
    permission: '/api/v1/aiPlugin/save',
  },
  {
    name: 'categorize',
    label: $t('plugin.button.categorize'),
    icon: markRaw(CategorizeIcon),
    permission: '/api/v1/aiPlugin/save',
  },
  {
    name: 'delete',
    label: $t('button.delete'),
    type: 'info',
    icon: markRaw(Delete),
    permission: '/api/v1/aiPlugin/remove',
  },
]);
const categoryList = ref([]);
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
// 处理操作按钮点击
const handleAction = ({ action, item }) => {
  // 根据不同的操作执行不同的逻辑
  switch (action.name) {
    case 'categorize': {
      categoryCategoryModal.value.openDialog(item);
      break;
    }
    case 'delete': {
      handleDelete(item);
      break;
    }
    case 'edit': {
      aiPluginModalRef.value.openDialog(item);
      break;
    }
    case 'tools': {
      router.push({
        path: '/ai/plugin/tools',
        query: {
          id: item.id,
          pageKey: '/ai/plugin',
        },
      });
      break;
    }
  }
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
const handleButtonClick = (event, _item) => {
  switch (event.key) {
    case 'add': {
      aiPluginModalRef.value.openDialog({});
      break;
    }
  }
};
const pluginCategoryId = ref('0');
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
  pageDataRef.value.setQuery({ category: item.item.id });
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
        <CategoryCrudPanel
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
        />
      </div>

      <div class="plugin-content-data-container">
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
              @action-click="handleAction"
            />
          </template>
        </PageData>
      </div>
    </div>
    <AddPluginModal ref="aiPluginModalRef" @reload="handleSearch" />
    <CategoryPluginModal ref="categoryCategoryModal" @reload="handleSearch" />
  </div>
</template>

<style scoped>
.knowledge-container {
  padding: 20px;
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
  height: calc(100vh - 161px);
  padding-top: 20px;
}
.category-panel-container {
}
.plugin-content-data-container {
  padding: 20px;
  background-color: var(--el-bg-color);
  width: 100%;
}
</style>
