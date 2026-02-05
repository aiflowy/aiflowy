<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import type { BotInfo } from '@aiflowy/types';

import type { ActionButton } from '#/components/page/CardList.vue';

import { computed, markRaw, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Plus, Setting } from '@element-plus/icons-vue';
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

import { removeBotFromId } from '#/api';
import { api } from '#/api/request';
import defaultAvatar from '#/assets/ai/bot/defaultBotAvatar.png';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardList from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import PageSide from '#/components/page/PageSide.vue';
import { useDictStore } from '#/store';

import Modal from './modal.vue';

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

onMounted(() => {
  initDict();
  getSideList();
});

const router = useRouter();
const pageDataRef = ref();
const modalRef = ref<InstanceType<typeof Modal>>();
const dictStore = useDictStore();

// 操作按钮配置
const headerButtons = [
  {
    key: 'create',
    text: `${$t('button.create')}${$t('bot.chatAssistant')}`,
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/documentCollection/save',
  },
];
const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '',
    onClick(row: BotInfo) {
      modalRef.value?.open('edit', row);
    },
  },
  {
    icon: Setting,
    text: $t('button.setting'),
    className: '',
    permission: '',
    onClick(row: BotInfo) {
      router.push({ path: `/ai/bots/setting/${row.id}` });
    },
  },
  {
    icon: Delete,
    text: $t('button.delete'),
    className: 'item-danger',
    permission: '/api/v1/bot/remove',
    onClick(row: BotInfo) {
      removeBot(row);
    },
  },
];

const removeBot = async (bot: BotInfo) => {
  const [action] = await tryit(ElMessageBox.confirm)(
    $t('message.deleteAlert'),
    $t('message.noticeTitle'),
    {
      confirmButtonText: $t('message.ok'),
      cancelButtonText: $t('message.cancel'),
      type: 'warning',
    },
  );

  if (!action) {
    const [err, res] = await tryit(removeBotFromId)(bot.id);

    if (!err && res.errorCode === 0) {
      ElMessage.success($t('message.deleteOkMessage'));
      pageDataRef.value.setQuery({});
    }
  }
};

const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
const handleButtonClick = () => {
  modalRef.value?.open('create');
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
function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
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
          .post('/api/v1/botCategory/remove', { id: row.id })
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
        ? '/api/v1/botCategory/update'
        : '/api/v1/botCategory/save';
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
  const [, res] = await tryit(api.get)('/api/v1/botCategory/list', {
    params: { sortKey: 'sortNo', sortType: 'asc' },
  });

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
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="handleButtonClick"
    />
    <div class="flex flex-1 gap-6">
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
          page-url="/api/v1/bot/page"
          :page-sizes="[12, 18, 24]"
          :page-size="12"
        >
          <template #default="{ pageList }">
            <CardList
              :default-icon="defaultAvatar"
              :data="pageList"
              :actions="actions"
            />
          </template>
        </PageData>
      </div>
    </div>
    <!-- 创建&编辑Bot弹窗 -->
    <Modal ref="modalRef" @success="pageDataRef.setQuery({})" />

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
