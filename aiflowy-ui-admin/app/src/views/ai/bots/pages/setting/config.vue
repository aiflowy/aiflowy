<script setup lang="ts">
import type { AiLlm, BotInfo } from '@aiflowy/types';

import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';
import { tryit } from '@aiflowy/utils';

import { Plus } from '@element-plus/icons-vue';
import { useDebounceFn } from '@vueuse/core';
import {
  ElCol,
  ElCollapse,
  ElCollapseItem,
  ElIcon,
  ElInputNumber,
  ElMessage,
  ElRow,
  ElSelect,
  ElSlider,
} from 'element-plus';

import { getAiLlmList, updateLlmId, updateLlmOptions } from '#/api';
import { api } from '#/api/request';
import CollapseViewItem from '#/components/collapseViewItem/CollapseViewItem.vue';
import CommonSelectDataModal from '#/components/commonSelectModal/CommonSelectDataModal.vue';

const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const route = useRoute();
const botId = ref<string>((route.params.id as string) || '');
const options = ref<AiLlm[]>([]);
const selectedId = ref<string>('');
const llmConfig = ref({
  maxMessageCount: 0,
  maxReplyLength: 0,
  temperature: 0,
  topK: 0,
  topP: 0,
});

watch(
  props,
  (newValue) => {
    if (!newValue.hasSavePermission) {
      selectedId.value = '没有权限';
    } else if (newValue.bot?.llmId) {
      selectedId.value = newValue.bot.llmId;
    }

    if (newValue.bot) {
      llmConfig.value = newValue.bot.llmOptions;
    }
  },
  { immediate: true },
);
const pluginToolIdsData = ref<any>([]);
const knowledgeIdsData = ref<any>([]);
const workflowIdsData = ref<any>([]);

const workflowData = ref<any[]>([]);
const knowledgeData = ref<any[]>([]);
const pluginToolData = ref<any[]>([]);
const getAiBotPluginToolList = async () => {
  api
    .post('/api/v1/aiPluginTool/tool/list', { botId: botId.value })
    .then((res) => {
      pluginToolData.value = res.data;
      pluginToolIdsData.value = res.data.map((item: any) => item.id);
    });
};
const getAiBotKnowledgeList = async () => {
  api
    .get('/api/v1/aiBotKnowledge/list', {
      params: {
        botId: botId.value,
      },
    })
    .then((res) => {
      knowledgeData.value = res.data.map((item: any) => {
        return {
          recordId: item.id,
          ...item.knowledge,
        };
      });
      knowledgeIdsData.value = res.data.map((item: any) => item.knowledgeId);
    });
};

const getAiBotWorkflowList = async () => {
  api
    .get('/api/v1/aiBotWorkflow/list', {
      params: {
        botId: botId.value,
      },
    })
    .then((res) => {
      workflowData.value = res.data.map((item: any) => {
        return {
          recordId: item.id,
          ...item.workflow,
        };
      });
      workflowIdsData.value = res.data.map((item: any) => item.workflowId);
    });
};

onMounted(async () => {
  const [, res] = await tryit(getAiLlmList({ supportFunctionCalling: true }));
  getAiBotPluginToolList();
  getAiBotKnowledgeList();
  getAiBotWorkflowList();
  if (res?.errorCode === 0) {
    options.value = res.data;
  }
});

const handleLlmChange = async (value: string) => {
  if (!props.bot) return;

  const [, res] = await tryit(
    updateLlmId({
      id: props.bot?.id || '',
      llmId: value,
    }),
  );

  if (res?.errorCode === 0) {
    ElMessage.success('保存成功');
  } else {
    ElMessage.error(res?.message || '保存失败');
  }
};
const handleLlmOptionsChange = useDebounceFn(
  (key: keyof typeof llmConfig.value, value: number) => {
    const _value = handleInvalidNumber(value);

    llmConfig.value.temperature = _value;
    updateLlmOptions({
      id: props.bot?.id || '',
      llmOptions: {
        [key]: _value,
      },
    });
  },
  300,
);

/** 处理不合规数值 */
const handleInvalidNumber = (
  value: number,
  min = 0.1,
  max = 1,
  decimal = 1,
) => {
  if (Number.isNaN(value) || value < min) {
    return min;
  } else if (value > max) {
    return max;
  }

  if (value.toString().includes('.')) {
    const factor = 10 ** decimal;
    return Math.floor(value * factor) / factor;
  }

  return value;
};
const pluginToolDataRef = ref();
const knowledgeDataRef = ref();
const workflowDataRef = ref();

const handleAddPlugin = () => {
  pluginToolDataRef.value.openDialog(pluginToolIdsData.value);
};
const handleAddKnowledge = () => {
  knowledgeDataRef.value.openDialog(knowledgeIdsData.value);
};
const handleAddWorkflow = () => {
  workflowDataRef.value.openDialog(workflowIdsData.value);
};
const confirmUpdateAiBotPlugin = (data: any) => {
  api
    .post('/api/v1/aiBotPlugins/updateBotPluginToolIds', {
      botId: botId.value,
      pluginToolIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotPluginToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const confirmUpdateAiBotKnowledge = (data: any) => {
  api
    .post('/api/v1/aiBotKnowledge/updateBotKnowledgeIds', {
      botId: botId.value,
      knowledgeIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotKnowledgeList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const confirmUpdateAiBotWorkflow = (data: any) => {
  api
    .post('/api/v1/aiBotWorkflow/updateBotWorkflowIds', {
      botId: botId.value,
      workflowIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotWorkflowList();
      } else {
        ElMessage.error(res.message);
      }
    });
};
const deletePluginTool = (item: any) => {
  api
    .post('/api/v1/aiBotPlugins/doRemove', {
      botId: botId.value,
      pluginToolId: item.id,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotPluginToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const deleteKnowledge = (item: any) => {
  api
    .post('/api/v1/aiBotKnowledge/remove', {
      id: item.recordId,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotKnowledgeList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const deleteWorkflow = (item: any) => {
  console.log('deleteWorkflow', item);
  api
    .post('/api/v1/aiBotWorkflow/remove', {
      id: item.recordId,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotWorkflowList();
      } else {
        ElMessage.error(res.message);
      }
    });
};
</script>

<template>
  <div class="flex flex-col gap-3">
    <!-- 大模型 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">大模型</h1>
      <div
        class="flex w-full flex-col justify-between gap-1 rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElSelect
          v-model="selectedId"
          :options="options"
          :props="{ value: 'id', label: 'title' }"
          :disabled="!hasSavePermission"
          @change="handleLlmChange"
        />
        <!-- 温度 -->
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">温度</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopK</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="1"
              :max="10"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topK"
              @change="
                (value) => handleLlmOptionsChange('topK', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="1"
              :max="10"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topK"
              @change="
                (value) => handleLlmOptionsChange('topK', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">TopP</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topP"
              @change="
                (value) => handleLlmOptionsChange('topP', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topP"
              @change="
                (value) => handleLlmOptionsChange('topP', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">最大回复长度</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="1024"
              :max="20000"
              :step="1000"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxReplyLength"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxReplyLength', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="1024"
              :max="20000"
              :step="1000"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxReplyLength"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxReplyLength', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle">
          <ElCol :span="6" class="">携带历史条数</ElCol>
          <ElCol :span="10">
            <ElSlider
              :min="1"
              :max="100"
              :step="10"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxMessageCount"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxMessageCount', value as number)
              "
            />
          </ElCol>
          <ElCol :span="8">
            <ElInputNumber
              :min="1"
              :max="100"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxMessageCount"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxMessageCount', value as number)
              "
            />
          </ElCol>
        </ElRow>
      </div>
    </div>

    <!-- 技能 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">技能</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="工作流">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>工作流</span>
                <span @click="handleAddWorkflow()">
                  <ElIcon>
                    <Plus />
                  </ElIcon>
                </span>
              </div>
            </template>
            <CollapseViewItem :data="workflowData" @delete="deleteWorkflow" />
          </ElCollapseItem>
          <ElCollapseItem title="知识库">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>知识库</span>
                <span @click="handleAddKnowledge()">
                  <ElIcon>
                    <Plus />
                  </ElIcon>
                </span>
              </div>
            </template>
            <CollapseViewItem :data="knowledgeData" @delete="deleteKnowledge" />
          </ElCollapseItem>
          <ElCollapseItem title="插件">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>插件</span>
                <span @click="handleAddPlugin()">
                  <ElIcon>
                    <Plus />
                  </ElIcon>
                </span>
              </div>
            </template>
            <CollapseViewItem
              :data="pluginToolData"
              title-key="name"
              @delete="deletePluginTool"
            />
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 对话设置 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="问题预设">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>问题预设</span>
                <ElIcon>
                  <Plus />
                </ElIcon>
              </div>
            </template>
          </ElCollapseItem>
          <ElCollapseItem title="欢迎语" />
        </ElCollapse>
      </div>
    </div>

    <!-- 发布 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div
        class="flex w-full flex-col justify-between rounded-lg bg-[#F7F7F7] p-3"
      >
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="嵌入" />
          <ElCollapseItem title="API" />
        </ElCollapse>
      </div>
    </div>

    <!-- 选择插件-->
    <CommonSelectDataModal
      ref="pluginToolDataRef"
      page-url="/api/v1/aiPlugin/pageByCategory"
      :is-select-plugin="true"
      @get-data="confirmUpdateAiBotPlugin"
      :extra-query-params="{
        category: 0,
      }"
    />

    <!-- 选择知识库-->
    <CommonSelectDataModal
      ref="knowledgeDataRef"
      page-url="/api/v1/aiKnowledge/page"
      @get-data="confirmUpdateAiBotKnowledge"
    />

    <!-- 选择工作流-->
    <CommonSelectDataModal
      ref="workflowDataRef"
      page-url="/api/v1/aiWorkflow/page"
      @get-data="confirmUpdateAiBotWorkflow"
    />
  </div>
</template>
