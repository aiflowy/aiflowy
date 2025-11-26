<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import type { ActionButton } from '#/components/page/CardList.vue';

import { onMounted, ref } from 'vue';

import { Delete, Edit, Plus, Tools, VideoPlay } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
} from 'element-plus';

import { api } from '#/api/request';
import workflowIcon from '#/assets/ai/workflow/workflowIcon.png';
import CardList from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import AiWorkflowModal from './AiWorkflowModal.vue';

const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    action: 'edit',
    className: '',
    permission: '',
  },
  {
    icon: Tools,
    text: $t('button.design'),
    action: 'design',
    className: '',
    permission: '',
  },
  {
    icon: VideoPlay,
    text: $t('button.run'),
    action: 'run',
    className: '',
    permission: '',
  },
  {
    icon: Delete,
    text: $t('button.delete'),
    action: 'delete',
    className: 'item-danger',
    permission: '',
  },
];
onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const saveDialog = ref();
const formInline = ref({
  title: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
}
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
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
              reset(formRef.value);
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
function handleAction(row: any, action: string) {
  switch (action) {
    case 'delete': {
      remove(row);
      break;
    }
    case 'design': {
      toDesignPage(row);
      break;
    }
    case 'edit': {
      showDialog(row);
      break;
    }
    case 'run': {
      break;
    }
  }
}
function toDesignPage(row: any) {
  router.push({
    name: 'WorkflowDesign',
    query: {
      id: row.id,
    },
  });
}
</script>

<template>
  <div class="page-container">
    <AiWorkflowModal ref="saveDialog" @reload="reset" />
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('aiWorkflow.title')" prop="title">
        <ElInput
          v-model="formInline.title"
          :placeholder="$t('aiWorkflow.title')"
        />
      </ElFormItem>
      <ElFormItem>
        <ElButton @click="search(formRef)" type="primary">
          {{ $t('button.query') }}
        </ElButton>
        <ElButton @click="reset(formRef)">
          {{ $t('button.reset') }}
        </ElButton>
      </ElFormItem>
    </ElForm>
    <div class="handle-div">
      <ElButton
        v-access:code="'/api/v1/aiWorkflow/save'"
        @click="showDialog({})"
        type="primary"
      >
        <ElIcon class="mr-1">
          <Plus />
        </ElIcon>
        {{ $t('button.add') }}
      </ElButton>
    </div>
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/aiWorkflow/page"
      :page-sizes="[9, 12, 18, 24]"
      :page-size="9"
    >
      <template #default="{ pageList }">
        <CardList
          :default-icon="workflowIcon"
          :data="pageList"
          :actions="actions"
          @on-action="handleAction"
        />
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
