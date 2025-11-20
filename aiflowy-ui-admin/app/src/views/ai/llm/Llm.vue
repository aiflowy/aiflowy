<script setup>
import { markRaw, nextTick, onMounted, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElIcon,
  ElImage,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { deleteLlm, getLlmBrandList } from '#/api/ai/llm.js';
import CategoryPanel from '#/components/categoryPanel/CategoryPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import LlmModal from '#/views/ai/llm/LlmModal.vue';
import QuickAddLlmModal from '#/views/ai/llm/QuickAddLlmModal.vue';

const brandListData = ref([]);
const LlmAddOrUpdateDialog = ref(false);
onMounted(() => {
  getLlmBrandList().then((res) => {
    brandListData.value = [
      { title: $t('common.allCategories'), key: '' },
      ...res.data,
    ];
  });
});

const handleCategoryClick = (category) => {
  pageDataRef.value.setQuery({ brand: category.key });
};

// 按钮配置
const headerButtons = ref([
  {
    key: 'add',
    text: $t('button.addLlm'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'addLlm' },
  },
  {
    key: 'edit',
    text: $t('button.oneClickAdd'),
    type: 'primary',
    icon: markRaw(Plus),
    data: { action: 'oneClickAdd' },
  },
]);

const LlmAddOrUpdateDialogRef = ref(null);

const addLlm = async (isEdit = false) => {
  await nextTick();
  if (LlmAddOrUpdateDialogRef.value) {
    if (isEdit) {
      LlmAddOrUpdateDialogRef.value.openUpdateDialog(editRecord.value);
    } else {
      LlmAddOrUpdateDialogRef.value.openAddDialog();
    }
  }
};
const oneClickAddLlmRef = ref();
const oneClickAdd = () => {
  oneClickAddLlmRef.value.openQuickAddLlmDialog();
};
// 处理搜索事件
const handleSearch = (searchValue) => {
  pageDataRef.value.setQuery({ title: searchValue, isQueryOr: true });
  // 执行搜索逻辑
};
// 处理按钮点击事件
const handleButtonClick = (event) => {
  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'add': {
      addLlm();
      break;
    }
    case 'edit': {
      oneClickAdd();
      break;
    }
  }
};

const pageDataRef = ref();

const handleDelete = (row) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      deleteLlm({ id: row.id }).then((res) => {
        if (res.errorCode === 0) {
          ElMessage.success(res.message);
          pageDataRef.value.setQuery();
        }
      });
    })
    .catch(() => {
      // 取消逻辑
    });
};

const editRecord = ref({});
const handleEdit = (row) => {
  editRecord.value = row;
  addLlm(true);
};
const handleSuccess = () => {
  pageDataRef.value.setQuery();
};
</script>

<template>
  <div class="llm-container">
    <div class="llm-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>

    <div class="llm-content">
      <div>
        <CategoryPanel
          :categories="brandListData"
          title-key="title"
          :use-img-for-svg="true"
          :expand-width="150"
          @click="handleCategoryClick"
        />
      </div>
      <div class="llm-table">
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/aiLlm/page"
          :page-size="10"
          :init-query-params="{ status: 1 }"
        >
          <template #default="{ pageList }">
            <ElTable :data="pageList" style="width: 100%" border size="large">
              <ElTableColumn prop="icon" label="Icon" width="80">
                <template #default="scope">
                  <ElImage
                    v-if="scope.row.icon"
                    :src="scope.row.icon"
                    style="width: 30px; height: 30px"
                  />
                </template>
              </ElTableColumn>
              <ElTableColumn
                prop="title"
                :label="$t('llm.filed.title')"
                width="180"
              />
              <ElTableColumn
                prop="supportFeatures"
                :label="$t('llm.filed.ability')"
                width="240"
              >
                <template #default="scope">
                  <ElTag
                    v-for="item in scope.row.supportFeatures"
                    :key="item"
                    class="mr-1"
                  >
                    {{ item }}
                  </ElTag>
                </template>
              </ElTableColumn>
              <ElTableColumn
                prop="description"
                :label="$t('llm.filed.description')"
                width="auto"
              />
              <ElTableColumn
                fixed="right"
                :label="$t('common.handle')"
                min-width="120"
              >
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
      </div>
    </div>

    <!--   大模型模态框-->
    <LlmModal
      :edit-record="editRecord"
      ref="LlmAddOrUpdateDialogRef"
      @success="handleSuccess"
      @close="LlmAddOrUpdateDialog = false"
    />
    <!--    一键添加大模型模态框-->
    <QuickAddLlmModal ref="oneClickAddLlmRef" @success="handleSuccess" />
  </div>
</template>

<style scoped>
.llm-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.llm-header {
  margin-bottom: 20px;
}

.llm-content {
  display: flex;
}

.llm-table {
  width: 100%;
}
</style>
