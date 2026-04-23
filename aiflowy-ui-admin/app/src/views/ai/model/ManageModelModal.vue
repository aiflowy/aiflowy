<script setup lang="ts">
import { nextTick, reactive, ref } from 'vue';

import {
  CirclePlus,
  Loading,
  Minus,
  RefreshRight,
} from '@element-plus/icons-vue';
import {
  ElCollapse,
  ElCollapseItem,
  ElDialog,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessageBox,
  ElTabPane,
  ElTabs,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
import ModelViewItemOperation from '#/views/ai/model/ModelViewItemOperation.vue';

const emit = defineEmits(['reload']);
const tabList = ref<any>([]);
const isLoading = ref(false);
const chatModelTabList = [
  // {
  //   label: $t('llm.all'),
  //   name: 'all',
  // },
  {
    label: $t('llmProvider.chatModel'),
    name: 'chatModel',
  },
  // {
  //   label: $t('llm.modelAbility.free'),
  //   name: 'supportFree',
  // },
];
const embeddingModelTabList = [
  {
    label: $t('llmProvider.embeddingModel'),
    name: 'embeddingModel',
  },
];

const rerankModelTabList = [
  {
    label: $t('llmProvider.rerankModel'),
    name: 'rerankModel',
  },
];

const ocrModelTabList = [
  {
    label: $t('llmProvider.ocrModel'),
    name: 'ocrModel',
  },
];
const formDataRef = ref();
const providerInfo = ref<any>();
const getProviderInfo = (id: string) => {
  api.get(`/api/v1/modelProvider/detail?id=${id}`).then((res) => {
    if (res.errorCode === 0) {
      providerInfo.value = res.data;
    }
  });
};
const modelList = ref<any>([]);
const getLlmList = (providerId: string, modelType: string) => {
  isLoading.value = true;
  const url =
    modelType === ''
      ? `/api/v1/model/selectLlmByProviderAndModelType?providerId=${providerId}&modelType=${modelType}&supportFree=true`
      : `/api/v1/model/selectLlmByProviderAndModelType?providerId=${providerId}&modelType=${modelType}&selectText=${searchFormDada.searchText}`;
  api.get(url).then((res) => {
    if (res.errorCode === 0) {
      const chatModelMap = res.data || {};
      modelList.value = Object.entries(chatModelMap).map(
        ([groupName, llmList]) => ({
          groupName,
          llmList,
        }),
      );
    }
    isLoading.value = false;
  });
};
const selectedProviderId = ref('');
defineExpose({
  // providerId: 供应商id， clickModelType 父组件点击的是什么类型的模型 可以是chatModel or embeddingModel
  openDialog(providerId: string, clickModelType: string) {
    switch (clickModelType) {
      case 'chatModel': {
        tabList.value = [...chatModelTabList];
        break;
      }
      case 'embeddingModel': {
        tabList.value = [...embeddingModelTabList];
        break;
      }
      case 'ocrModel': {
        tabList.value = [...ocrModelTabList];
        break;
      }
      case 'rerankModel': {
        tabList.value = [...rerankModelTabList];
        break;
      }
      // No default
    }
    selectedProviderId.value = providerId;
    formDataRef.value?.resetFields();
    modelList.value = [];
    activeName.value = tabList.value[0]?.name;
    getProviderInfo(providerId);
    getLlmList(providerId, clickModelType);
    dialogVisible.value = true;
  },
  openEditDialog(item: any) {
    dialogVisible.value = true;
    isAdd.value = false;
    formData.icon = item.icon;
    formData.providerName = item.providerName;
    formData.provider = item.provider;
  },
});
const isAdd = ref(true);
const dialogVisible = ref(false);
const formData = reactive({
  icon: '',
  providerName: '',
  provider: '',
  apiKey: '',
  endPoint: '',
  chatPath: '',
  embedPath: '',
});
const closeDialog = () => {
  dialogVisible.value = false;
};
const handleTabClick = async () => {
  await nextTick();
  getLlmList(providerInfo.value.id, activeName.value);
};
const activeName = ref('all');
const handleGroupNameDelete = (groupName: string) => {
  ElMessageBox.confirm(
    $t('message.deleteModelGroupAlert'),
    $t('message.noticeTitle'),
    {
      confirmButtonText: $t('message.ok'),
      cancelButtonText: $t('message.cancel'),
      type: 'warning',
    },
  ).then(() => {
    api
      .post(`/api/v1/model/removeByEntity`, {
        groupName,
        providerId: selectedProviderId.value,
      })
      .then((res) => {
        if (res.errorCode === 0) {
          getLlmList(providerInfo.value.id, activeName.value);
          emit('reload');
        }
      });
  });
};
const handleDeleteLlm = (id: any) => {
  ElMessageBox.confirm(
    $t('message.deleteModelAlert'),
    $t('message.noticeTitle'),
    {
      confirmButtonText: $t('message.ok'),
      cancelButtonText: $t('message.cancel'),
      type: 'warning',
    },
  ).then(() => {
    api.post(`/api/v1/model/removeLlmByIds`, { id }).then((res) => {
      if (res.errorCode === 0) {
        getLlmList(providerInfo.value.id, activeName.value);
        emit('reload');
      }
    });
  });
};
const handleAddLlm = (id: string) => {
  api
    .post(`/api/v1/model/update`, {
      id,
      withUsed: true,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        getLlmList(providerInfo.value.id, activeName.value);
        emit('reload');
      }
    });
};
const searchFormDada = reactive({
  searchText: '',
});
const handleAddAllLlm = () => {
  api
    .post(`/api/v1/model/addAllLlm`, {
      providerId: selectedProviderId.value,
      withUsed: true,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        getLlmList(providerInfo.value.id, activeName.value);
        emit('reload');
      }
    });
};
const handleRefresh = () => {
  if (isLoading.value) return;
  getLlmList(providerInfo.value.id, activeName.value);
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="`${providerInfo?.providerName}${$t('llmProvider.model')}`"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
    width="762"
  >
    <div class="manage-llm-container">
      <div class="form-container">
        <ElForm ref="formDataRef" :model="searchFormDada" status-icon>
          <ElFormItem prop="searchText">
            <div class="search-container">
              <ElInput
                v-model.trim="searchFormDada.searchText"
                @input="handleRefresh"
                :placeholder="$t('llm.searchTextPlaceholder')"
              />
              <ElTooltip
                :content="$t('llm.button.addAllLlm')"
                placement="top"
                effect="dark"
              >
                <ElIcon
                  size="20"
                  @click="handleAddAllLlm"
                  class="cursor-pointer"
                >
                  <CirclePlus />
                </ElIcon>
              </ElTooltip>
              <ElTooltip
                :content="$t('llm.button.RetrieveAgain')"
                placement="top"
                effect="dark"
              >
                <ElIcon size="20" @click="handleRefresh" class="cursor-pointer">
                  <RefreshRight />
                </ElIcon>
              </ElTooltip>
            </div>
          </ElFormItem>
        </ElForm>
      </div>
      <div class="llm-table-container">
        <ElTabs v-model="activeName" @tab-click="handleTabClick">
          <ElTabPane
            :label="item.label"
            :name="item.name"
            v-for="item in tabList"
            default-active="all"
            :key="item.name"
          >
            <div v-if="isLoading" class="collapse-loading">
              <ElIcon class="is-loading" size="24">
                <Loading />
              </ElIcon>
            </div>
            <div v-else>
              <ElCollapse
                expand-icon-position="left"
                v-if="modelList.length > 0"
              >
                <ElCollapseItem
                  v-for="group in modelList"
                  :key="group.groupName"
                  :title="group.groupName"
                  :name="group.groupName"
                >
                  <template #title>
                    <div class="flex items-center justify-between pr-2">
                      <span>{{ group.groupName }}</span>
                      <span>
                        <ElIcon
                          @click.stop="handleGroupNameDelete(group.groupName)"
                        >
                          <Minus />
                        </ElIcon>
                      </span>
                    </div>
                  </template>
                  <ModelViewItemOperation
                    :need-hidden-setting-icon="true"
                    :llm-list="group.llmList"
                    @delete-llm="handleDeleteLlm"
                    @add-llm="handleAddLlm"
                    :is-management="true"
                  />
                </ElCollapseItem>
              </ElCollapse>
            </div>
          </ElTabPane>
        </ElTabs>
      </div>
    </div>
  </ElDialog>
</template>

<style scoped>
.manage-llm-container {
  height: 540px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.form-container {
  height: 30px;
}
.search-container {
  width: 100%;
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}
.llm-table-container {
  flex: 1;
}
.collapse-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  gap: 12px;
  color: var(--el-text-color-secondary);
}
:deep(.el-tabs__nav-wrap::after) {
  height: 1px !important;
  background-color: #e4e7ed !important;
}
</style>
